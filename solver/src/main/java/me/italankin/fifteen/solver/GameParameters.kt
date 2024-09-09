package me.italankin.fifteen.solver

import me.italankin.fifteen.game.Game

class GameParameters(val game: Game) {

    @JvmField
    val width: Int = game.width

    @JvmField
    val height: Int = game.height

    @JvmField
    val size: Int = game.size

    /**
     * Goal state
     */
    @JvmField
    val goal: IntArray = game.goal.toIntArray()

    /**
     * Mapping of numbers to their goal positions, e.g. for a game with goal:
     *
     * ```
     * 1 2
     * 3 0
     * ```
     *
     * The result will be:
     *
     * ```
     * [3, 0, 1, 2]
     * ```
     */
    @JvmField
    val goalIndices: IntArray = IntArray(goal.size)

    init {
        for (i in goal.indices) {
            goalIndices[goal[i]] = i
        }
    }
}
