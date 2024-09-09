package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test

class FrozenGameGeneratorTest {

    @Test
    fun freeze() {
        val generator = randomGames().generator().bounded(5).freeze()
        val first = generator.toList()
        val second = generator.toList()
        for (i in 0 until 5) {
            assertEquals(first[i].state, second[i].state)
        }
    }
}
