package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.ClassicGame
import me.italankin.fifteen.solver.GameParameters
import org.junit.Assert.assertEquals
import org.junit.Test

class InversionsTest {

    @Test
    fun calc() {
        val game = ClassicGame(
            4, 4,
            listOf(
                3, 10, 7, 6,
                9, 0, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            )
        )
        val actual = Inversions().calc(game.state.toIntArray(), GameParameters(game))
        assertEquals(32, actual)
    }
}

