package me.italankin.fifteen.game

import org.junit.Assert.assertEquals
import org.junit.Test

class BaseGameTest {

    @Test
    fun move_single() {
        val game = BaseGame(
            4, 4,
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        val newIndex = game.move(1)
        assertEquals(0, newIndex)
        assertEquals(
            listOf(
                3, 0, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            game.state
        )
    }

    @Test
    fun move_invalid() {
        val game = BaseGame(
            4, 4,
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        val newIndex = game.move(7)
        assertEquals(7, newIndex)
        assertEquals(
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            game.state
        )
    }

    @Test
    fun move_indices() {
        val game = BaseGame(
            4, 4,
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        val newIndices = game.move(listOf(1, 2, 3))
        assertEquals(listOf(0, 1, 2), newIndices)
        assertEquals(
            listOf(
                3, 7, 6, 0,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            game.state
        )
    }

    @Test
    fun move_indices_invalid() {
        val game = BaseGame(
            4, 4,
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        val newIndices = game.move(listOf(3, 7))
        assertEquals(listOf(3, 7), newIndices)
        assertEquals(
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            game.state
        )
    }

    @Test
    fun findMovingTiles() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 10, 7, 6,
                9, 0, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            emptyList<Int>(),
            game.findMovingTiles(1, Game.Direction.UP)
        )
        assertEquals(
            listOf(1),
            game.findMovingTiles(1, Game.Direction.DOWN)
        )
        assertEquals(
            listOf(9, 13),
            game.findMovingTiles(13, Game.Direction.UP)
        )
        assertEquals(
            listOf(4),
            game.findMovingTiles(4, Game.Direction.RIGHT)
        )
        assertEquals(
            listOf(4),
            game.findMovingTiles(4, Game.Direction.DEFAULT)
        )
    }

    @Test
    fun possibleMoves_TL() {
        val game = BaseGame(
            4, 4,
            listOf(
                0, 3, 7, 6,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(1, 4),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_TR() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 0,
                9, 10, 5, 1,
                2, 11, 8, 4,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(2, 7),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_BL() {
        val game = BaseGame(
            4, 4,
            listOf(
                9, 3, 7, 6,
                2, 10, 5, 1,
                12, 11, 8, 4,
                0, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(13, 8),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_BR() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 1,
                9, 10, 5, 4,
                2, 11, 8, 14,
                12, 15, 13, 0,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(14, 11),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_R() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 1,
                9, 10, 5, 4,
                2, 11, 8, 0,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(10, 7, 15),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_L() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 1,
                9, 10, 5, 4,
                0, 2, 11, 8,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(9, 4, 12),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_T() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 0, 6, 1,
                9, 7, 5, 4,
                2, 10, 11, 8,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(0, 2, 5),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_B() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 1,
                9, 10, 5, 4,
                2, 15, 11, 8,
                12, 0, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(12, 14, 9),
            game.possibleMoves()
        )
    }

    @Test
    fun possibleMoves_M() {
        val game = BaseGame(
            4, 4,
            listOf(
                3, 7, 6, 1,
                9, 10, 5, 4,
                2, 0, 11, 8,
                12, 15, 13, 14,
            ),
            ClassicGame.goal(4, 4)
        )
        assertEquals(
            listOf(8, 10, 5, 13),
            game.possibleMoves()
        )
    }
}
