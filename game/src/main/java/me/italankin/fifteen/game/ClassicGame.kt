package me.italankin.fifteen.game

import me.italankin.fifteen.game.Game.Scrambler

/**
 * Classic variation:
 * ```
 * 1 2 3
 * 4 5 6
 * 7 8 -
 * ```
 */
class ClassicGame : BaseGame {

    companion object {

        @JvmStatic
        fun goal(width: Int, height: Int): List<Int> {
            val size = width * height
            val result: MutableList<Int> = ArrayList(size)
            for (i in 0 until size) {
                result.add((i + 1) % size)
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
