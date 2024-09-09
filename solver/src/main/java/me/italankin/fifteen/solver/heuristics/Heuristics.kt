package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.solver.GameParameters

interface Heuristics {

    /**
     * Calculate initial heuristic value
     */
    fun calc(state: IntArray, params: GameParameters): Int

    /**
     * Calculate new heuristic value, given its previous value
     */
    fun calc(
        state: IntArray,
        params: GameParameters,
        prevValue: Int,
        prevZeroIndex: Int,
        newZeroIndex: Int
    ): Int = calc(state, params)

    override fun toString(): String
}

operator fun Heuristics.plus(other: Heuristics): Heuristics {
    return object : Heuristics {
        override fun calc(state: IntArray, params: GameParameters): Int {
            return this@plus.calc(state, params) + other.calc(state, params)
        }

        override fun calc(
            state: IntArray,
            params: GameParameters,
            prevValue: Int,
            prevZeroIndex: Int,
            newZeroIndex: Int
        ): Int {
            return this@plus.calc(state, params, prevValue, prevZeroIndex, newZeroIndex) +
                    other.calc(state, params, prevValue, prevZeroIndex, newZeroIndex)
        }

        override fun toString(): String = "${this@plus} + $other"
    }
}

operator fun Int.times(heuristics: Heuristics): Heuristics {
    return object : Heuristics {
        override fun calc(state: IntArray, params: GameParameters): Int {
            return this@times * heuristics.calc(state, params)
        }

        override fun calc(
            state: IntArray,
            params: GameParameters,
            prevValue: Int,
            prevZeroIndex: Int,
            newZeroIndex: Int
        ): Int {
            return this@times * heuristics.calc(state, params, prevValue, prevZeroIndex, newZeroIndex)
        }

        override fun toString(): String = "${this@times} * ($heuristics)"
    }
}

operator fun Heuristics.times(multiplier: Int): Heuristics {
    return multiplier.times(this)
}

operator fun Heuristics.div(divider: Int): Heuristics {
    return object : Heuristics {
        override fun calc(state: IntArray, params: GameParameters): Int {
            return this@div.calc(state, params) / divider
        }

        override fun calc(
            state: IntArray,
            params: GameParameters,
            prevValue: Int,
            prevZeroIndex: Int,
            newZeroIndex: Int
        ): Int {
            return this@div.calc(state, params, prevValue, prevZeroIndex, newZeroIndex) / divider
        }

        override fun toString(): String = "(${this@div}) / $divider"
    }
}
