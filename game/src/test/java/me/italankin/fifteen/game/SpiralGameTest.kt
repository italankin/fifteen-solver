package me.italankin.fifteen.game

import org.junit.Assert.assertEquals
import org.junit.Test

class SpiralGameTest {

    @Test
    fun goal() {
        assertEquals(
            listOf(
                1, 2, 3,
                8, 0, 4,
                7, 6, 5
            ),
            SpiralGame.goal(3, 3)
        )
        assertEquals(
            listOf(
                1, 2, 3, 4,
                12, 13, 14, 5,
                11, 0, 15, 6,
                10, 9, 8, 7
            ),
            SpiralGame.goal(4, 4)
        )
    }
}
