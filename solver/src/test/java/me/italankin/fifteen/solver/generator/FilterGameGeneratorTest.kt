package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FilterGameGeneratorTest {

    @Test
    fun bounded() {
        val baseGenerator = randomGames()
            .scrambler(ShuffleScrambler(0))
            .generator()
            .bounded(5)
            .freeze()
        val unfiltered = baseGenerator.toList()
        val filtered = baseGenerator.filterGames { it.state[0] == 1 }.toList()
        assertNotEquals(unfiltered.size, filtered.size)
        assertEquals(1, filtered.size)
    }

    @Test
    fun unbounded() {
        val games = randomGames()
            .scrambler(ShuffleScrambler(0))
            .generator()
            .filterGames { it.state[0] == 1 }
            .bounded(5)
            .toList()
        assertEquals(5, games.size)
        for (game in games) {
            assertEquals(1, game.state[0])
        }
    }

    @Test
    fun unboundedNoMatch() {
        val generator = object : GameGenerator {
            override fun iterator(): Iterator<Game> {
                return iterator {
                    yield(TestGame(1, true))
                    yield(TestGame(2, true))
                    yield(TestGame(3, true))
                    yield(TestGame(4, true))
                }
            }

            override fun toString() = ""
        }
        val games = generator.filterGames { !it.isSolved }.toList()
        assertEquals(0, games.size)
    }
}
