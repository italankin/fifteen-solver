package me.italankin.fifteen.solver

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import me.italankin.fifteen.game.Game
import java.util.concurrent.atomic.AtomicInteger

internal class SolversPool(
    private val solvers: List<Solver>
) {

    private val inQueue = AtomicInteger()
    private val inProgress = AtomicInteger()
    private val done = AtomicInteger()

    private val queueState = MutableStateFlow(QueueState(0, 0, 0))

    fun enqueue(game: Game): Iterable<Flow<Result<Solver.Solution>>> {
        return solvers.map { solver ->
            queueState.value = QueueState(inQueue.incrementAndGet(), inProgress.get(), done.get())
            flow {
                queueState.value = QueueState(inQueue.decrementAndGet(), inProgress.incrementAndGet(), done.get())
                try {
                    val solution = solver.solve(game)
                    emit(Result.success(solution))
                } catch (e: Throwable) {
                    emit(Result.failure(SolveException(game, solver, e)))
                }
                queueState.value = QueueState(inQueue.get(), inProgress.decrementAndGet(), done.incrementAndGet())
            }
        }
    }

    fun queueState(): Flow<QueueState> {
        return queueState
    }
}

class SolveException(
    val game: Game,
    val solver: Solver,
    cause: Throwable
) : RuntimeException("Solve failed: ${cause.message}", cause) {

    override fun fillInStackTrace(): Throwable? = null
}
