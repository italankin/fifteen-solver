package me.italankin.fifteen.solver.reporter

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.solver.QueueState
import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.SolveException
import me.italankin.fifteen.solver.Solver

interface Reporter {

    object NoOp : Reporter

    fun onSessionStarted(session: Session) = Unit
    fun onSessionSolveStarted(session: Session, game: Game) = Unit
    fun onSessionSolutionFound(session: Session, solution: Solver.Solution) = Unit
    fun onSessionSolutionError(session: Session, error: SolveException) = Unit
    fun onSessionQueueStateUpdated(session: Session, queueState: QueueState) = Unit
    fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) = Unit
    fun onSessionCancelled(session: Session) = Unit
}
