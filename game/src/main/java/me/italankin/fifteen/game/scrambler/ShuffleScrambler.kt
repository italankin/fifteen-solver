package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game.Scrambler
import kotlin.random.Random

/**
 * Random shuffle
 */
class ShuffleScrambler(
    private val random: Random = Random
) : Scrambler {

    constructor(seed: Long) : this(Random(seed))

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) {
        outState.shuffle(random)
    }

    override fun toString(): String {
        return "ShuffleScrambler"
    }
}
