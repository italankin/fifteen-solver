package me.italankin.fifteen.solver.reporter

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.solver.QueueState
import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.SolveException
import me.italankin.fifteen.solver.Solver

class CompositeReporter(
    private vararg val reporters: Reporter
) : Reporter {

    override fun onSessionStarted(session: Session) {
        for (reporter in reporters) {
            reporter.onSessionStarted(session)
        }
    }

    override fun onSessionSolveStarted(session: Session, game: Game) {
        for (reporter in reporters) {
            reporter.onSessionSolveStarted(session, game)
        }
    }

    override fun onSessionSolutionFound(session: Session, solution: Solver.Solution) {
        for (reporter in reporters) {
            reporter.onSessionSolutionFound(session, solution)
        }
    }

    override fun onSessionSolutionError(session: Session, error: SolveException) {
        for (reporter in reporters) {
            reporter.onSessionSolutionError(session, error)
        }
    }

    override fun onSessionQueueStateUpdated(session: Session, queueState: QueueState) {
        for (reporter in reporters) {
            reporter.onSessionQueueStateUpdated(session, queueState)
        }
    }

    override fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) {
        for (reporter in reporters) {
            reporter.onSessionFinished(session, results)
        }
    }

    override fun onSessionCancelled(session: Session) {
        for (reporter in reporters) {
            reporter.onSessionCancelled(session)
        }
    }
}

operator fun Reporter.plus(reporter: Reporter): Reporter {
    return CompositeReporter(this, reporter)
}
