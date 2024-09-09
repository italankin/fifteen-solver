package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.euclidean
import me.italankin.fifteen.solver.GameParameters
import kotlin.math.roundToInt

/**
 * Sum of Euclidean distances to tiles' goal positions
 */
class EuclideanDistance : Heuristics {

    override fun calc(state: IntArray, params: GameParameters): Int {
        val width = params.width
        var distance = 0f
        for ((i, n) in state.withIndex()) {
            distance += euclidean(
                params.goalIndices[n] % width,
                params.goalIndices[n] / width,
                i % width,
                i / width
            )
        }
        return distance.roundToInt()
    }

    override fun toString(): String {
        return "EuclideanDistance"
    }
}
