package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test

class StaticGameGeneratorTest {

    @Test
    fun staticGames() {
        val generator = staticGames {
            +TestGame(1)
            +TestGame(2)
            +TestGame(3)
        }
        assertEquals(
            listOf(TestGame(1), TestGame(2), TestGame(3)),
            generator.toList()
        )
    }
}
