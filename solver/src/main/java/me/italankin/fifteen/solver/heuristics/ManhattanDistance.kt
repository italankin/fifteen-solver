package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.manhattan
import me.italankin.fifteen.solver.GameParameters

/**
 * Sum of [manhattan] distances to tiles' goal positions
 */
class ManhattanDistance : Heuristics {

    override fun calc(state: IntArray, params: GameParameters): Int {
        val width = params.width
        val goalIndices = params.goalIndices
        var distance = 0
        for (i in state.indices) {
            val n = state[i]
            if (n == 0) continue
            val index = goalIndices[n]
            distance += manhattan(i % width, i / width, index % width, index / width)
        }
        return distance
    }

    override fun calc(
        state: IntArray,
        params: GameParameters,
        prevValue: Int,
        prevZeroIndex: Int,
        newZeroIndex: Int
    ): Int {
        val width = params.width
        val index = params.goalIndices[state[prevZeroIndex]]
        return prevValue -
                manhattan(newZeroIndex % width, newZeroIndex / width, index % width, index / width) +
                manhattan(prevZeroIndex % width, prevZeroIndex / width, index % width, index / width)
    }

    override fun toString(): String = "ManhattanDistance"
}
