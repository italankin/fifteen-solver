package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test

class BoundedGameGeneratorImplTest {

    @Test
    fun count() {
        val generator = randomGames().generator().bounded(3)
        assertEquals(3, generator.count)
        assertEquals(3, generator.count())
    }

    @Test
    fun min() {
        val generator = randomGames().generator().take(4).toGenerator()
        val underTest = generator.bounded(10)
        assertEquals(4, underTest.count)
        assertEquals(4, underTest.count())
    }
}
