package me.italankin.fifteen.solver.reporter

import kotlinx.coroutines.*
import me.italankin.fifteen.solver.QueueState
import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.Solver
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

private val MIN_INTERVAL = 1.seconds

/**
 * Reports current session solving state to [System.out]
 *
 * @param interval interval at which progress will be reported
 */
class ProgressReporter(
    private val interval: Duration = MIN_INTERVAL
) : Reporter {

    private val progressDispatcher = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "ProgressThread")
    }.asCoroutineDispatcher()
    private val progressScope = CoroutineScope(progressDispatcher)

    @Volatile
    private var queueState: QueueState = QueueState(0, 0, 0)

    init {
        if (interval < MIN_INTERVAL) {
            throw IllegalArgumentException("interval must be >= $MIN_INTERVAL")
        }
    }

    override fun onSessionStarted(session: Session) {
        val totalCount = session.stats.totalCount
        val startTime = System.currentTimeMillis()
        val etaEstimator = EtaEstimator(totalCount)

        progressScope.launch {
            do {
                val sb = StringBuilder()
                sb.append("\rqueue: ${queueState.inQueue}/${queueState.inProgress}/${queueState.done}/$totalCount")
                val now = System.currentTimeMillis()
                val elapsed = now - startTime
                val done = queueState.done.toFloat()
                val progress = done / totalCount
                sb.append(" (${(progress * 100).toInt()}%)")
                sb.append(", %.3f games/s".format(done / elapsed * 1000f))
                sb.append(", memory: ${session.stats.memoryCurrent shr 20} MB")
                sb.append(", elapsed: ")
                sb.append(elapsed.milliseconds.toString(DurationUnit.SECONDS))
                sb.append(", remaining: ")
                val eta = etaEstimator.eta(now, queueState.done)
                if (eta != null && !eta.isInfinite()) {
                    sb.append(eta.toString(DurationUnit.SECONDS))
                } else {
                    sb.append("N/A")
                }

                print(sb.toString())

                delay(interval)
            } while (queueState.done < totalCount)
        }
    }

    override fun onSessionQueueStateUpdated(session: Session, queueState: QueueState) {
        this.queueState = queueState
    }

    override fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) {
        print("\r")
        System.out.flush()

        stop()
    }

    override fun onSessionCancelled(session: Session) {
        stop()
    }

    private fun stop() {
        progressScope.cancel()
        progressDispatcher.close()
    }

    class EtaEstimator(
        private val total: Int,
        private val windowMs: Long = 20_000L
    ) {
        private val samples = ArrayDeque<Pair<Long, Int>>() // (timestamp, doneCount)

        fun eta(now: Long, done: Int): Duration? {
            samples += now to done
            while (samples.first().first < now - windowMs) {
                samples.removeFirst()
            }
            if (samples.size < 2 || done == 0) {
                return null
            }
            val (timestamp, doneCount) = samples.first()
            val speed = (done - doneCount).toDouble() / (now - timestamp) // games/ms
            val remainingMs = ((total - done) / speed).toLong()
            return remainingMs.milliseconds
        }
    }
}

fun Reporter.withProgress(): Reporter {
    if (this is ProgressReporter) {
        return this
    }
    return ProgressReporter() + this
}
