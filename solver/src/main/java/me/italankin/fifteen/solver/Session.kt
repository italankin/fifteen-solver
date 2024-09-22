package me.italankin.fifteen.solver

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.italankin.fifteen.solver.generator.BoundedGameGenerator
import me.italankin.fifteen.solver.reporter.Reporter
import me.italankin.fifteen.solver.reporter.SystemOutReporter
import java.lang.management.ManagementFactory
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Session(
    val generator: BoundedGameGenerator,
    val solvers: List<Solver>,
    val reporter: Reporter = SystemOutReporter(),
    val concurrency: Concurrency = Concurrency.AvailableProcessors()
) {

    val id: String = System.identityHashCode(this).toString(16)
    val stats = Stats()

    private val statsCollector = StatsCollector()
    private val executed = AtomicBoolean()

    init {
        if (solvers.isEmpty()) {
            throw IllegalArgumentException("`solvers` must not be empty")
        }
    }

    override fun equals(other: Any?): Boolean = other is Session && id == other.id

    override fun hashCode(): Int = id.hashCode()

    /**
     * Dump parameters to [System.out]
     */
    fun dumpParameters() {
        println("session parameters:")
        println("  concurrency: $concurrency")
        println("  generator:\n${generator.toString().prependIndent("    ")}")
        println("  games count: ${generator.count}")
        println("  total games count: ${stats.totalCount}")
        println("  solvers:")
        println(solvers.joinToString("\n").prependIndent("    * "))
        println()
    }

    /**
     * Start solving!
     *
     * Each session can be executed only once.
     *
     * @return list of solutions
     */
    @OptIn(FlowPreview::class)
    suspend fun execute(): List<Result<Solver.Solution>> {
        if (!executed.compareAndSet(false, true)) {
            throw IllegalStateException("session was already executed")
        }
        return coroutineScope {
            val solversPool = SolversPool(solvers)
            val solverQueueEvents = launch {
                solversPool.queueState().collect { reporter.onSessionQueueStateUpdated(this@Session, it) }
            }
            val dispatcher = Executors
                .newFixedThreadPool(concurrency.numThreads, SolverThreadFactory(concurrency.threadPriority))
                .asCoroutineDispatcher()

            statsCollector.start()
            reporter.onSessionStarted(this@Session)
            val solutions = generator
                .flatMap { game ->
                    reporter.onSessionSolveStarted(this@Session, game)
                    solversPool.enqueue(game)
                }
                .asFlow()
                .flattenMerge(concurrency.numThreads)
                .flowOn(dispatcher)
                .onEach { result ->
                    result
                        .onSuccess { reporter.onSessionSolutionFound(this@Session, it) }
                        .onFailure { reporter.onSessionSolutionError(this@Session, it as SolveException) }
                }
                .onCompletion { e: Throwable? ->
                    dispatcher.close()
                    solverQueueEvents.cancel()
                    if (e != null) {
                        statsCollector.cancel()
                        reporter.onSessionCancelled(this@Session)
                    }
                }
                .toList()
            statsCollector.stop()

            reporter.onSessionFinished(this@Session, solutions)

            solutions
        }
    }

    /**
     * [execute] this session in the calling thread
     */
    fun executeBlocking(): List<Result<Solver.Solution>> = runBlocking { execute() }

    abstract class Concurrency {

        /**
         * Number of threads
         */
        abstract val numThreads: Int

        /**
         * Solving [Thread] priority, a value between [Thread.MIN_PRIORITY] and [Thread.MAX_PRIORITY]
         */
        abstract val threadPriority: Int

        override fun toString(): String {
            return "${javaClass.simpleName}(numThreads=${numThreads}, threadPriority=${threadPriority})"
        }

        /**
         * Number of available processor, as in [Runtime.availableProcessors]
         */
        class AvailableProcessors(
            override val threadPriority: Int = Thread.MAX_PRIORITY
        ) : Concurrency() {
            override val numThreads: Int
                get() = Runtime.getRuntime().availableProcessors()
        }

        /**
         * Same as [AvailableProcessors], but halved
         */
        class HalfAvailableProcessors(
            override val threadPriority: Int = Thread.MAX_PRIORITY
        ) : Concurrency() {
            override val numThreads: Int = Runtime.getRuntime().availableProcessors() / 2
        }

        /**
         * Use fixed [number of threads][numThreads]
         */
        class Fixed(
            override val numThreads: Int,
            override val threadPriority: Int = Thread.MAX_PRIORITY
        ) : Concurrency() {

            init {
                if (numThreads <= 0) throw IllegalArgumentException("numThreads must be > 0")
            }
        }
    }

    inner class Stats {
        /**
         * Total games count
         */
        val totalCount: Int = solvers.size * generator.count

        /**
         * Average memory use (in MB)
         */
        val memoryAvg: Long
            get() = statsCollector.memoryAvg

        /**
         * Max memory use (in MB)
         */
        val memoryMax: Long
            get() = statsCollector.memoryMax

        /**
         * Current memory use (in MB)
         */
        val memoryCurrent: Long
            get() = statsCollector.memoryCurrent

        /**
         * Number of GC invocations
         */
        val gcCount: Long
            get() = statsCollector.gcCount

        /**
         * Number of GC invocation time
         */
        val gcTimeMs: Long
            get() = statsCollector.gcTimeMs

        /**
         * Total session time, may be `-1` if session is still in progress
         */
        val totalTimeMs: Long
            get() = statsCollector.totalTimeMs
    }

    private class SolverThreadFactory(private val priority: Int) : ThreadFactory {
        private val threadCounter = AtomicInteger()

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(r, "SolverThread-${threadCounter.getAndIncrement()}")
            thread.priority = priority
            return thread
        }
    }

    private class StatsCollector {

        private val runtime = Runtime.getRuntime()
        private var startTime = -1L

        @Volatile
        private var memorySamples = 0

        private val memoryWatcherDispatcher = Executors.newSingleThreadScheduledExecutor { r ->
            val thread = Thread(r, "MemorySampler")
            thread.priority = Thread.MIN_PRIORITY
            thread
        }.asCoroutineDispatcher()
        private val memoryWatcherScope = CoroutineScope(memoryWatcherDispatcher)

        @Volatile
        var memoryAvg = 0L
            private set

        @Volatile
        var memoryMax = 0L
            private set

        @Volatile
        var memoryCurrent = 0L
            private set

        @Volatile
        var gcCount = 0L
            private set

        @Volatile
        var gcTimeMs = 0L
            private set

        @Volatile
        var totalTimeMs: Long = -1
            private set

        fun start() {
            if (startTime != -1L) {
                throw IllegalStateException("run is already started")
            }
            startTime = System.currentTimeMillis()
            sampleMemory()

            memoryWatcherScope.launch {
                while (isActive) {
                    sampleMemory()
                    delay(1000)
                }
            }
        }

        fun stop() {
            if (totalTimeMs != -1L) {
                throw IllegalStateException("run is already ended")
            }
            if (startTime == -1L) {
                throw IllegalStateException("run is not started")
            }
            sampleMemory()
            totalTimeMs = System.currentTimeMillis() - startTime

            var gcTime = 0L
            var gcCount = 0L
            for (gcBean in ManagementFactory.getGarbageCollectorMXBeans()) {
                gcCount += gcBean.collectionCount
                gcTime += gcBean.collectionTime
            }
            this.gcCount = gcCount
            this.gcTimeMs = gcTime

            cancel()
        }

        fun cancel() {
            memoryWatcherDispatcher.close()
            memoryWatcherScope.cancel()
        }

        private fun sampleMemory() {
            if (startTime == -1L || totalTimeMs != -1L) {
                return
            }
            val sample = runtime.totalMemory() - runtime.freeMemory()
            if (sample > memoryMax) {
                memoryMax = sample
            }
            val sum = memoryAvg * (memorySamples++) + sample
            memoryAvg = (sum.toDouble() / memorySamples).toLong()
            memoryCurrent = sample
        }
    }
}
