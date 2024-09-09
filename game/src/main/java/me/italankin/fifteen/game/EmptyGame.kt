package me.italankin.fifteen.game

object EmptyGame : Game {
    override val state: List<Int> = emptyList()
    override val goal: List<Int> = emptyList()
    override val width: Int = 0
    override val height: Int = 0
    override val moves: Int = 0
    override val isSolved: Boolean = false
    override val size: Int = 0
    override fun move(x: Int, y: Int): Int = -1
    override fun move(index: Int): Int = -1
    override fun findMovingTiles(startIndex: Int, direction: Game.Direction): List<Int> = emptyList()
    override fun possibleMoves(): List<Int> = emptyList()
    override fun move(indices: List<Int>): List<Int> = indices
    override fun getDirection(index: Int): Game.Direction = Game.Direction.DEFAULT
    override fun addStateCallback(callback: Game.StateCallback) = Unit
    override fun removeStateCallback(callback: Game.StateCallback) = Unit
}
