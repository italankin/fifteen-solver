package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class ShufflingGameGeneratorTest {

    @Test
    fun shuffle() {
        val generator = listOf(TestGame(1), TestGame(2), TestGame(3))
            .toGenerator()
            .shuffle(Random(0))
        assertEquals(
            listOf(TestGame(2), TestGame(3), TestGame(1)),
            generator.toList()
        )
        assertEquals(
            listOf(TestGame(3), TestGame(2), TestGame(1)),
            generator.toList()
        )
    }
}
