package me.italankin.fifteen.solver

import kotlinx.coroutines.runBlocking
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import me.italankin.fifteen.solver.algorithm.astar.AStar
import me.italankin.fifteen.solver.generator.bounded
import me.italankin.fifteen.solver.generator.randomGames
import me.italankin.fifteen.solver.generator.x
import me.italankin.fifteen.solver.heuristics.ManhattanDistance
import me.italankin.fifteen.solver.reporter.Reporter
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionTest {

    @Test
    fun test(): Unit = runBlocking {
        val session = Session(
            generator = randomGames()
                .size(3 x 3)
                .scrambler(ShuffleScrambler(0))
                .generator()
                .bounded(5),
            solvers = listOf(Solver(ManhattanDistance(), AStar())),
            reporter = Reporter.NoOp,
            concurrency = Session.Concurrency.Fixed(1)
        )
        val solutions = session.execute()
        assertEquals(5, solutions.size)
    }
}
