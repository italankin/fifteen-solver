package me.italankin.fifteen.solver

import me.italankin.fifteen.game.ClassicGame
import me.italankin.fifteen.game.SnakeGame
import me.italankin.fifteen.game.SpiralGame
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class GameParametersTest {

    @Test
    fun goalIndices_classic() {
        val game = ClassicGame(4, 4, false, ShuffleScrambler())
        val params = GameParameters(game)
        println(params.goalIndices.contentToString())
        assertArrayEquals(
            intArrayOf(15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
            params.goalIndices
        )
    }

    @Test
    fun goalIndices_snake() {
        val game = SnakeGame(4, 4, false, ShuffleScrambler())
        val params = GameParameters(game)
        println(params.goalIndices.contentToString())
        assertArrayEquals(
            intArrayOf(12, 0, 1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 15, 14, 13),
            params.goalIndices
        )
    }

    @Test
    fun goalIndices_spiral() {
        val game = SpiralGame(4, 4, false, ShuffleScrambler())
        val params = GameParameters(game)
        println(params.goalIndices.contentToString())
        assertArrayEquals(
            intArrayOf(9, 0, 1, 2, 3, 7, 11, 15, 14, 13, 12, 8, 4, 5, 6, 10),
            params.goalIndices
        )
    }
}
