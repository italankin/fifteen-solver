package me.italankin.fifteen.solver.heuristics

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.solver.GameParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals

fun testIncremental(
    game: Game,
    moveIndex: Int,
    heuristics: Heuristics
) {
    val params = GameParameters(game)
    val oldZeroIndex = game.state.indexOf(0)
    val oldValue = heuristics.calc(game.state.toIntArray(), params)
    val newIndex = game.move(moveIndex)
    // make sure we actually made a move
    assertNotEquals(moveIndex, newIndex)
    val newValue = heuristics.calc(game.state.toIntArray(), params, oldValue, oldZeroIndex, game.state.indexOf(0))
    assertEquals(heuristics.calc(game.state.toIntArray(), params), newValue)
}
