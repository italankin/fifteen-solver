package me.italankin.fifteen.solver.algorithm.fringe

import me.italankin.fifteen.solver.Node
import me.italankin.fifteen.solver.algorithm.Algorithm
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet
import java.util.*

/**
 * @see <a href="https://en.wikipedia.org/wiki/Fringe_search">Wikipedia</a>
 */
class FringeSearch : Algorithm {

    override fun run(start: Node): Algorithm.Result {
        var now = ArrayDeque<SearchNode>() // nodes with f <= limit
        var later = ArrayDeque<SearchNode>() // nodes with f > limit
        val closed = IntHashSet() // visited nodes
        var explored = 0L

        val startNode = SearchNode(start, 0)
        var limit = startNode.f
        var nextLimit = Int.MAX_VALUE // smallest f that exceeded current limit
        now.add(startNode)

        while (true) {
            if (now.isEmpty()) {
                if (later.isEmpty()) throw IllegalStateException("No solution found")
                // advance to the next f‑layer
                limit = nextLimit
                nextLimit = Int.MAX_VALUE
                now = later
                later = ArrayDeque()
                continue
            }

            val current = now.removeFirst()

            if (!closed.add(current.node.hashCode())) continue
            explored++

            if (current.node.isGoal) {
                val unexplored = now.size + later.size
                return Algorithm.Result(current.node, explored, unexplored.toLong())
            }

            for (child in current.node.children()) {
                if (child == null) continue
                if (child.hashCode() in closed) continue

                val childNode = SearchNode(child, current.g + 1)
                if (childNode.f <= limit) {
                    now.add(childNode)
                } else {
                    later.add(childNode)
                    if (childNode.f < nextLimit) {
                        nextLimit = childNode.f
                    }
                }
            }
        }
    }

    override fun toString(): String = "FringeSearch"

    private class SearchNode(val node: Node, val g: Int) {
        val f: Int = g + node.heuristicsValue
    }
}
