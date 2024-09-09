package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.inversions
import me.italankin.fifteen.solver.GameParameters

class Inversions : Heuristics {

    override fun calc(state: IntArray, params: GameParameters): Int {
        return inversions(state)
    }

    override fun toString(): String = "Inversions"
}
