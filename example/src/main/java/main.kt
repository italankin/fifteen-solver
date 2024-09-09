package main

import kotlinx.coroutines.runBlocking
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.Solver
import me.italankin.fifteen.solver.algorithm.astar.AStar
import me.italankin.fifteen.solver.generator.bounded
import me.italankin.fifteen.solver.generator.randomGames
import me.italankin.fifteen.solver.generator.x
import me.italankin.fifteen.solver.heuristics.LinearConflict
import me.italankin.fifteen.solver.heuristics.ManhattanDistance
import me.italankin.fifteen.solver.reporter.SystemOutReporter
import me.italankin.fifteen.solver.reporter.withProgress

fun main(): Unit = runBlocking {
    val session = Session(
        generator = randomGames()
            .size(3 x 3)
            .scrambler(ShuffleScrambler())
            .generator()
            .bounded(100),
        solvers = listOf(
            Solver(
                heuristics = ManhattanDistance(),
                algorithm = AStar(),
            ),
            Solver(
                heuristics = LinearConflict(),
                algorithm = AStar(),
            ),
        ),
        reporter = SystemOutReporter(
            compareBy = listOf(SystemOutReporter.DefaultFields.AVG_TIME)
        ).withProgress(),
    )
    session.dumpParameters()
    session.execute()
}
