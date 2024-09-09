package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

/**
 * Game generator, potentially unbounded
 */
interface GameGenerator : Iterable<Game> {

    override fun toString(): String

    data class Size(val width: Int, val height: Int) {
        companion object {
            val SIZE_4X4 = Size(4, 4)
        }

        val size: Int = width * height

        val lastIndex: Int = size - 1

        init {
            if (width < 2) {
                throw IllegalArgumentException("width must be >= 2")
            }
            if (height < 2) {
                throw IllegalArgumentException("height must be >= 2")
            }
        }

        override fun toString(): String = "${width}x${height}"
    }
}

/**
 * Bounded [GameGenerator], which produces [BoundedGameGenerator.count] (or less) games
 */
interface BoundedGameGenerator : GameGenerator {
    val count: Int
}

infix fun Int.x(height: Int): GameGenerator.Size {
    return GameGenerator.Size(this, height)
}
