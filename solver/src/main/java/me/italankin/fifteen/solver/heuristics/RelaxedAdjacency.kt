package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.solver.GameParameters
import me.italankin.fifteen.solver.util.swap

/**
 * Repeatedly swap misplaced tiles into their positions, ignoring board rules
 */
class RelaxedAdjacency : Heuristics {

    override fun calc(state: IntArray, params: GameParameters): Int {
        val goal = params.goal
        val goalIndices = params.goalIndices
        var distance = 0
        val tmp = state.copyOf()
        var zeroIdx = tmp.indexOf(0)
        while (!tmp.contentEquals(goal)) {
            if (zeroIdx == goalIndices[0]) {
                // find first non-empty tile
                for (i in tmp.indices) {
                    if (goal[i] == tmp[i]) continue
                    tmp.swap(i, zeroIdx)
                    zeroIdx = i
                    break
                }
            } else {
                // select next tile at the current zero pos
                val i = tmp.indexOf(goal[zeroIdx])
                tmp.swap(i, zeroIdx)
                zeroIdx = i
            }
            distance++
        }
        return distance
    }

    override fun toString(): String = "RelaxedAdjacency"
}
