package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game.Scrambler
import java.util.*
import kotlin.math.sign
import kotlin.random.Random

/**
 * Randomly click on a tile which either in the same row, or same column as empty space.
 *
 * @param rounds number of rounds to perform
 * @param random [Random] instance to use for choosing target cells
 */
class RandomClickScrambler(
    private val rounds: Int,
    private val random: Random = Random
) : Scrambler {

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) {
        val height = outState.size / width
        var zeroIndex = outState.indexOf(0)
        repeat(rounds) {
            val zx = zeroIndex % width
            val zy = zeroIndex / width
            var targetIndex: Int
            if (random.nextBoolean()) {
                // move horizontally
                do {
                    targetIndex = zy * width + random.nextInt(width)
                } while (zeroIndex == targetIndex)
                val dx = ((targetIndex % width) - zx).sign
                while (zeroIndex != targetIndex) {
                    val newZeroIndex = zeroIndex + dx
                    Collections.swap(outState, zeroIndex, newZeroIndex)
                    zeroIndex = newZeroIndex
                }
            } else {
                // move vertically
                do {
                    targetIndex = zx + width * random.nextInt(height)
                } while (zeroIndex == targetIndex)
                val dy = ((targetIndex / width) - zy).sign
                while (zeroIndex != targetIndex) {
                    val newZeroIndex = zeroIndex + width * dy
                    Collections.swap(outState, zeroIndex, newZeroIndex)
                    zeroIndex = newZeroIndex
                }
            }
        }
    }

    override fun toString(): String {
        return "RandomClickScrambler(rounds=$rounds)"
    }
}
