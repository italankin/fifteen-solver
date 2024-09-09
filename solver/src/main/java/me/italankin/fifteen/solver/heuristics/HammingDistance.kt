package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.solver.GameParameters

/**
 * Number of misplaced tiles
 */
class HammingDistance : Heuristics {

    override fun calc(state: IntArray, params: GameParameters): Int {
        val goal = params.goal
        var distance = 0
        for (i in goal.indices) {
            val actual = state[i]
            val expected = goal[i]
            if (expected == 0) continue
            if (actual != expected) {
                distance++
            }
        }
        return distance
    }

    override fun toString(): String = "HammingDistance"
}
