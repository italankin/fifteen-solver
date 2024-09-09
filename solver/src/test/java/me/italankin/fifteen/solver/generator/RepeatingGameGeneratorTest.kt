package me.italankin.fifteen.solver.generator

import org.junit.Assert.assertEquals
import org.junit.Test

class RepeatingGameGeneratorTest {

    @Test
    fun repeat() {
        val baseGenerator = (0 until 10).map(::TestGame).toGenerator()
        val repeated = baseGenerator.repeat(2)
        assertEquals(20, repeated.count)
        val repeatedGames = repeated.toList()
        val firstHalf = repeatedGames.take(10)
        val secondHalf = repeatedGames.drop(10)
        repeat(10) { index ->
            assertEquals(repeatedGames[index].state, firstHalf[index].state)
            assertEquals(repeatedGames[10 + index].state, secondHalf[index].state)
        }
    }

    @Test
    fun count() {
        val testGenerator = TestGenerator(listOf(TestGame(0)).toGenerator())
        val generator = testGenerator.repeat(5)
        assertEquals(5, generator.count)
        generator.toList()
        assertEquals(5, testGenerator.produced)
    }
}
