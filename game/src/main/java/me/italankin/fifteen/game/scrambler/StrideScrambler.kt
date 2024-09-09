package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game.Scrambler
import java.util.*
import kotlin.math.sign
import kotlin.random.Random

/**
 * Randomly select a cell and move `0` to that cell.
 *
 * @param iterations number of iterations to perform
 * @param allowEmptyMoves allow empty moves (selected target index matches index of `0`)
 * @param random [Random] instance for selecting next target index
 */
class StrideScrambler(
    private val iterations: Int,
    private val allowEmptyMoves: Boolean = false,
    private val random: Random = Random
) : Scrambler {

    init {
        if (iterations <= 0) {
            throw IllegalArgumentException("iterations must be > 0")
        }
    }

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) {
        var zeroIndex = outState.indexOf(0)
        repeat(iterations) {
            var targetIndex: Int
            do {
                targetIndex = random.nextInt(outState.size)
            } while (allowEmptyMoves || zeroIndex == targetIndex)
            var dx = targetIndex % width - zeroIndex % width
            while (dx != 0) {
                val d = dx.sign
                Collections.swap(outState, zeroIndex, zeroIndex + d)
                zeroIndex += d
                dx -= d
            }
            var dy = targetIndex / width - zeroIndex / width
            while (dy != 0) {
                val d = dy.sign
                Collections.swap(outState, zeroIndex, zeroIndex + d * width)
                zeroIndex += d * width
                dy -= d
            }
        }
    }

    override fun toString(): String {
        return "StrideScrambler(iterations=$iterations, allowEmptyMoves=$allowEmptyMoves)"
    }
}
