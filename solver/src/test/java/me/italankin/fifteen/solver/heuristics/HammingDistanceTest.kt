package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.ClassicGame
import me.italankin.fifteen.solver.GameParameters
import org.junit.Assert.assertEquals
import org.junit.Test

class HammingDistanceTest {

    @Test
    fun calc() {
        val game = ClassicGame(
            4, 4,
            listOf(
                3, 10, 7, 6,
                5, 9, 0, 1,
                2, 11, 8, 4,
                12, 15, 14, 13,
            )
        )
        val actual = HammingDistance().calc(game.state.toIntArray(), GameParameters(game))
        assertEquals(14, actual)
    }
}

