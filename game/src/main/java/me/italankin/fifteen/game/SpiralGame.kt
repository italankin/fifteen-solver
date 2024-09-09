package me.italankin.fifteen.game

import me.italankin.fifteen.game.Game.Scrambler

/**
 * Clockwise inwards:
 * ```
 * 1 2 3
 * 8 - 4
 * 7 6 5
 * ```
 */
class SpiralGame : BaseGame {

    companion object {

        @JvmStatic
        fun goal(width: Int, height: Int): List<Int> {
            var h = height
            var w = width
            val size = width * height
            var number = 1
            var r = 0
            var c = 0 // start row and column
            val array = Array(h) { IntArray(w) }
            while (r < h && c < w) {
                for (i in c until w) {
                    array[r][i] = number++ % size
                }
                r++
                for (i in r until h) {
                    array[i][w - 1] = number++ % size
                }
                w--
                if (r < h) {
                    for (i in w - 1 downTo c) {
                        array[h - 1][i] = number++ % size
                    }
                    h--
                }
                if (c < w) {
                    for (i in h - 1 downTo r) {
                        array[i][c] = number++ % size
                    }
                    c++
                }
            }
            val result: MutableList<Int> = ArrayList(size)
            for (i in 0 until height) {
                for (j in 0 until width) {
                    result.add(array[i][j])
                }
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

    constructor(width: Int, height: Int, state: List<Int>) : super(width, height, state, goal(width, height))

    constructor(width: Int, height: Int, state: List<Int>, moves: Int) : super(
        width,
        height,
        state,
        goal(width, height),
        moves
    )
}
