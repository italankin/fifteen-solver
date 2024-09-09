package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game

class SolvedScrambler : Game.Scrambler {

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) = Unit

    override fun toString(): String {
        return "SolvedScrambler"
    }
}
