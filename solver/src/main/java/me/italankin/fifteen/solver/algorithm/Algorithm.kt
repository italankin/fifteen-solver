package me.italankin.fifteen.solver.algorithm

import me.italankin.fifteen.solver.Node

interface Algorithm {

    /**
     * Find a solution for [start] node
     */
    fun run(start: Node): Result

    /**
     * @param path
     * @param nodesExplored explored (visited) nodes count
     * @param nodesUnexplored unexplored (expanded, but not visited) nodes count
     */
    class Result(val path: Node, val nodesExplored: Long, val nodesUnexplored: Long)
}
