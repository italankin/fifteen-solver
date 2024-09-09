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

        progressScope.launch {
            do {
                val sb = StringBuilder()
                sb.append("\rqueue: ${queueState.inQueue}/${queueState.inProgress}/${queueState.done}/$totalCount")
                val elapsed = System.currentTimeMillis() - startTime
                val done = queueState.done.toFloat()
                val progress = done / totalCount
                sb.append(" (${(progress * 100).toInt()}%)")
                sb.append(", %.3f games/s".format(done / elapsed * 1000f))
                sb.append(", memory: ${session.stats.memoryCurrent shr 20} MB")
                sb.append(", elapsed: ")
                val elapsedDur = elapsed.milliseconds
                sb.append(elapsedDur.toString(DurationUnit.SECONDS))
                sb.append(", remaining: ")
                if (progress > 0f) {
                    val remaining = ((1.0 / progress) * elapsed).milliseconds - elapsedDur
                    sb.append(remaining.toString(DurationUnit.SECONDS))
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
}

fun Reporter.withProgress(): Reporter {
    if (this is ProgressReporter) {
        return this
    }
    return ProgressReporter() + this
}
