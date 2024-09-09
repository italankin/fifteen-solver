package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test

class ConcatGameGeneratorTest {

    @Test
    fun concat() {
        val generator = concatGames {
            +staticGames { +TestGame(1) }
            +staticGames { +TestGame(2) }
        }
        assertEquals(2, generator.count)
        assertEquals(2, generator.count())
    }
}
