package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.solver.GameParameters

/**
 * Linear conflict heuristic, based on CUCS-219-85.
 *
 * Implementation detail: instead of two-step process, as described in the original paper, this implementation does
 * calculation in one pass. When we encounter a linear conflict, we only count the first one for each tile - because
 * later we count number of tiles we need to remove to get rid of the conflicts. If a tile creates two conflicts, we
 * will remove it one time, so the result will be equivalent to original method.
 */
class LinearConflict : Heuristics {

    companion object {

        /**
         * @return linear conflicts in [state] (excluding manhattan distance part)
         */
        @JvmStatic
        fun linearConflicts(state: IntArray, params: GameParameters): Int {
            val width = params.width
            var removeCount = 0 // tiles we need to remove to resolve linear conflict
            for ((tki, tk) in state.withIndex()) {
                if (tk == 0) continue
                val tkx = tki % width
                val tky = tki / width
                // rows conflicts
                // see if tk is the same row, as in goal
                if (tky == params.goalIndices[tk] / width) {
                    for (dx in (tkx + 1) until width) {
                        val tj = state[width * tky + dx]
                        if (tj == 0) continue
                        // tj is in the same row in goal position as tk
                        if (tky == params.goalIndices[tj] / width
                            // compare goal indices to find out if tk comes before tj in goal position
                            && params.goalIndices[tk] > params.goalIndices[tj]
                        ) {
                            removeCount++
                            break // see class javadoc
                        }
                    }
                }
                // conflicts in columns
                if (tkx == params.goalIndices[tk] % width) {
                    for (i in (tky + 1) until params.height) {
                        val tj = state[tkx + width * i]
                        if (tj == 0) continue
                        if (tkx == params.goalIndices[tj] % width && params.goalIndices[tk] > params.goalIndices[tj]) {
                            removeCount++
                            break // see class javadoc
                        }
                    }
                }
            }
            return removeCount * 2
        }
    }

    private val manhattanDistance = ManhattanDistance()

    override fun calc(state: IntArray, params: GameParameters): Int {
        return linearConflicts(state, params) + manhattanDistance.calc(state, params)
    }


    override fun toString(): String {
        return "LinearConflict"
    }
}
