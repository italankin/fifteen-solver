package me.italankin.fifteen.game.scrambler

import me.italankin.fifteen.game.Game.Scrambler
import java.util.*
import kotlin.random.Random

/**
 * Scramble puzzle by doing random moves
 *
 * @param moves number of moves to perform
 * @param allowPrevMoveUndo whether to allow previous move undoing, when the same tile moves back and forth consequently
 * @param random [Random] instance for selecting move directions
 */
class RandomMovesScrambler(
    private val moves: Int,
    private val allowPrevMoveUndo: Boolean = true,
    private val random: Random = Random
) : Scrambler {

    init {
        if (moves <= 0) {
            throw IllegalArgumentException("moves must be > 0")
        }
    }

    override fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>) {
        fun Int.outOfBounds(): Boolean {
            return this < 0 || this >= outState.size
        }

        var zeroIndex = outState.indexOf(0)
        var prevZeroIndex = zeroIndex
        var outOfRow = false
        repeat(moves) {
            var targetIndex: Int
            do {
                val d = Direction.values().random(random)
                targetIndex = zeroIndex + d.dy * width
                if (targetIndex.outOfBounds()) continue
                val targetRow = targetIndex / width
                targetIndex += d.dx
                outOfRow = targetIndex / width != targetRow
            } while (outOfRow || targetIndex.outOfBounds() || !allowPrevMoveUndo && targetIndex == prevZeroIndex)
            Collections.swap(outState, zeroIndex, targetIndex)
            prevZeroIndex = zeroIndex
            zeroIndex = targetIndex
        }
    }

    override fun toString(): String {
        return "RandomMovesScrambler(moves=$moves, allowPrevMoveUndo=$allowPrevMoveUndo)"
    }

    private enum class Direction(val dx: Int, val dy: Int) {
        LEFT(-1, 0),
        TOP(0, -1),
        RIGHT(1, 0),
        BOTTOM(0, 1),
    }
}
