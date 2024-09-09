package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomGameGeneratorTest {

    @Test
    fun skipSolved() {
        val generator = randomGames()
            .size(2 x 2)
            .scrambler(ShuffleScrambler(0))
            .skipSolved(true)
            .generator()
        val games = generator.bounded(10).toList()
        assertFalse(games.all(Game::isSolved))
    }

    @Test
    fun noSkipSolved() {
        val generator = randomGames()
            .size(2 x 2)
            .scrambler(ShuffleScrambler(1))
            .skipSolved(false)
            .generator()
        val games = generator.bounded(10).toList()
        assertTrue(games.any(Game::isSolved))
    }
}
