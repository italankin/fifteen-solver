package me.italankin.fifteen.game

import me.italankin.fifteen.game.Game.Scrambler

/**
 * Snake-like pattern:
 * ```
 * 1 2 3
 * 6 5 4
 * 7 8 -
 * ```
 */
class SnakeGame : BaseGame {

    companion object {

        @JvmStatic
        fun goal(width: Int, height: Int): List<Int> {
            val size = width * height
            val result: MutableList<Int> = ArrayList(size)
            for (i in 0 until size) {
                val row = i / width
                val n = if (row % 2 == 0) {
                    i + 1
                } else {
                    (width * (1 + i / width) - i % width)
                }
                result.add(n % size)
            }
            return result
        }
    }

    constructor(width: Int, height: Int, randomMissingTile: Boolean, scrambler: Scrambler) : super(
        width,
        height,
        goal(width, height),
        randomMissingTile,
        scrambler
    )

    constructor(width: Int, height: Int, missingTile: Int, scrambler: Scrambler) : super(
        width,
        height,
        goal(width, height),
        missingTile,
        scrambler
    )

    constructor(width: Int, height: Int, state: List<Int>, moves: Int) : super(
        width,
        height,
        state,
        goal(width, height),
        moves
    )

    constructor(width: Int, height: Int, state: List<Int>) : super(width, height, state, goal(width, height))
}
