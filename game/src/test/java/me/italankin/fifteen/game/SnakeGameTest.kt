package me.italankin.fifteen.game

import org.junit.Assert.assertEquals
import org.junit.Test

class SnakeGameTest {

    @Test
    fun goal() {
        assertEquals(
            listOf(
                1, 2, 3,
                6, 5, 4,
                7, 8, 0
            ),
            SnakeGame.goal(3, 3)
        )
        assertEquals(
            listOf(
                1, 2, 3, 4,
                8, 7, 6, 5,
                9, 10, 11, 12,
                0, 15, 14, 13
            ),
            SnakeGame.goal(4, 4)
        )
    }
}
