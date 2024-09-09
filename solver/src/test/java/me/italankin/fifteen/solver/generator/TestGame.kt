package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.EmptyGame
import me.italankin.fifteen.game.Game

class TestGame(
    private val id: Any,
    override val isSolved: Boolean = false
) : Game by EmptyGame {

    override fun equals(other: Any?): Boolean {
        return other is TestGame && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TestGame($id)"
}
