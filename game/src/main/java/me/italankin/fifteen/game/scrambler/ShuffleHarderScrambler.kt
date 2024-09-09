package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game.Scrambler
import java.util.*
import kotlin.random.Random

/**
 * Compared to [ShuffleScrambler], creates scrambles which require 20% more moves (on 4x4) to solve on average.
 */
class ShuffleHarderScrambler(
    private val random: Random = Random
) : Scrambler {

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) {
        outState.reverse()
        for (i in outState.size downTo 1) {
            if (random.nextFloat() > 1f - 1f / outState[i - 1]) {
                Collections.swap(outState, i - 1, random.nextInt(i))
            }
        }
    }

    override fun toString(): String {
        return "ShuffleHarderScrambler"
    }
}
