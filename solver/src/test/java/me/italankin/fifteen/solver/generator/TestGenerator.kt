package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

class TestGenerator(
    private val generator: BoundedGameGenerator
) : BoundedGameGenerator {

    var produced: Int = 0
        private set

    override val count: Int = generator.count

    override fun iterator(): Iterator<Game> {
        return CountingIterator(generator.iterator(), onProduce = { produced++ })
    }

    override fun toString(): String = "TestGenerator($generator)"
}

private class CountingIterator(
    private val delegate: Iterator<Game>,
    private val onProduce: () -> Unit
) : Iterator<Game> {

    override fun hasNext(): Boolean {
        return delegate.hasNext()
    }

    override fun next(): Game {
        val next = delegate.next()
        onProduce()
        return next
    }
}
