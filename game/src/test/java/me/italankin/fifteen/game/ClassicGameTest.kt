package me.italankin.fifteen.game

import org.junit.Assert.assertEquals
import org.junit.Test

class ClassicGameTest {

    @Test
    fun goal() {
        assertEquals(
            listOf(
                1, 2, 3,
                4, 5, 6,
                7, 8, 0
            ),
            ClassicGame.goal(3, 3)
        )
        assertEquals(
            listOf(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 0
            ),
            ClassicGame.goal(4, 4)
        )
    }
}
