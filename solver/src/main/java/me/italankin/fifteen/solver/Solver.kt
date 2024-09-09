package me.italankin.fifteen.solver

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.solver.algorithm.Algorithm
import me.italankin.fifteen.solver.heuristics.Heuristics
import me.italankin.fifteen.solver.util.toString

class Solver(
    val heuristics: Heuristics,
    val algorithm: Algorithm,
    private val name: String? = null
) {

    fun solve(game: Game): Solution {
        val start = System.nanoTime()
        val startNode = Node(game, heuristics)
        val result = algorithm.run(startNode)
        val time = System.nanoTime() - start
        return Solution(game, this@Solver, result.path, time, result.nodesExplored, result.nodesUnexplored)
    }

    override fun toString(): String {
        return name ?: "Solver(heuristics=$heuristics, algorithm=$algorithm)"
    }

    class Solution(
        val game: Game,
        val solver: Solver,
        val end: Node,
        time: Long,
        val nodesExplored: Long,
        val nodesUnexplored: Long
    ) {

        val moves: Int = end.moves

        /**
         * Spent time (in milliseconds)
         */
        val time: Float = time / 1_000_000f

        val start: Node by lazy {
            var p = end
            while (p.parent != null) {
                p = p.parent!!
            }
            p
        }

        val path: List<Node> by lazy { end.path }

        /**
         * Solution in the form of moved numbers, e.g.:
         * ```
         * [4, 7, 1, 6, 8, 5, 3, 1, 7, 4, 1, 8, 5, 3, 2, 1, 4, 7, 8, 5, 6]
         * ```
         */
        val solution: List<Int> by lazy {
            path.subList(1, path.size).map(Node::lastMovedNumber)
        }

        /**
         * Solution in the form of `L`eft, `R`ight, `U`p and `D`own directions, e.g.:
         * ```
         * LUURRDLULDRURDDLURULL
         * ```
         */
        val symbolicSolution: List<String> by lazy {
            path.subList(1, path.size)
                .map { node ->
                    val numberIndex = node.state.indexOf(node.lastMovedNumber())
                    when (numberIndex - node.zeroIndex) {
                        1 -> "R"
                        -1 -> "L"
                        node.gameParameters.width -> "D"
                        -node.gameParameters.width -> "U"
                        else -> throw IllegalStateException("there is a bug, this should not happen")
                    }
                }
        }

        override fun toString(): String {
            return buildString {
                append("scramble: ${start.state.joinToString()}\n")
                append(start.toString().prependIndent("  "))
                append('\n')
                append("solver: ${solver}\n")
                append("moves: ${moves}\n")
                append("time: ${time.toString(3)} ms\n")
                append("nodesExplored: $nodesExplored (${(nodesExplored / time).toString(3)} nodes/ms)\n")
                append("solution: ${solution.joinToString()}\n")
                append("end:\n${end.toString().prependIndent("  ")}")
            }
        }
    }
}
