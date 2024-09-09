package me.italankin.fifteen.game

import kotlin.math.abs

interface Game {

    /**
     * Current state of the game
     */
    val state: List<Int>

    /**
     * Goal of the game
     */
    val goal: List<Int>

    val width: Int

    val height: Int

    /**
     * Current number of moves made
     */
    val moves: Int

    val isSolved: Boolean

    /**
     * [width] * [height]
     */
    val size: Int

    /**
     * Attempt to move a tile at ([x], [y]), if possible
     *
     * @return new index of the tile, or the old index, if the move cannot be made
     */
    fun move(x: Int, y: Int): Int

    /**
     * Attempt to move a tile at [index], if possible
     *
     * @return new index of the tile, or the old index, if the move cannot be made
     */
    fun move(index: Int): Int

    /**
     * Move multiple tiles in **one** move.
     *
     * @param indices tile indices to move, consequently
     * @return new (or old, if a move was not possible) indices of moved tiles; has the same size as [indices]
     */
    fun move(indices: List<Int>): List<Int>

    /**
     * Find tiles we can move in a given [direction], starting with tile at [startIndex].
     *
     * Given field:
     * ```
     * 1 2 3
     * 4 5 6
     * 7 - 8
     * ```
     * and [startIndex] of `1` (number 2), and [Direction.DOWN],
     * the result will be `[1, 4]`
     *
     * @return a list of indices of possible tiles to move
     */
    fun findMovingTiles(startIndex: Int, direction: Direction): List<Int>

    /**
     * Indices of tiles which can be moved
     */
    fun possibleMoves(): List<Int>

    /**
     * @return direction in which tile at [index] can be moved
     */
    fun getDirection(index: Int): Direction

    fun addStateCallback(callback: StateCallback)

    fun removeStateCallback(callback: StateCallback)

    enum class Direction {
        DEFAULT,
        UP,
        RIGHT,
        DOWN,
        LEFT;

        companion object {

            /**
             * @return [Direction] by [dx] and [dy]
             */
            @JvmStatic
            fun of(dx: Float, dy: Float): Direction {
                if (dx == 0f && dy == 0f) {
                    return DEFAULT
                }
                return if (abs(dx) > abs(dy)) {
                    if (dx > 0) RIGHT else LEFT
                } else {
                    if (dy > 0) DOWN else UP
                }
            }
        }
    }

    interface StateCallback {
        fun onGameStateUpdated(game: Game) = Unit
        fun onTileMoved(game: Game, number: Int) = Unit
        fun onTilesMoved(game: Game, numbers: IntArray) = Unit
    }

    interface Scrambler {

        /**
         * Modifies starting [outState] to start the game with.
         *
         * @param width width of the puzzle
         * @param outState starting (and resulting) position
         * @param goal  goal of the game
         */
        fun scramble(width: Int, goal: List<Int>, outState: MutableList<Int>)
    }
}
