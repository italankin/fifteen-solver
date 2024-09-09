package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import kotlin.math.min

/**
 * Request maximum of [count] games from [generator]
 */
class BoundedGameGeneratorImpl(
    private val generator: GameGenerator,
    count: Int
) : BoundedGameGenerator {

    init {
        if (count < 0) {
            throw IllegalArgumentException("count must be >= 0")
        }
    }

    override val count: Int = if (generator is BoundedGameGenerator) {
        min(count, generator.count)
    } else {
        count
    }

    override fun iterator(): Iterator<Game> = Drain(generator.iterator(), count)

    override fun toString(): String {
        return """Bounded(count=$count) {
            |${generator.toString().prependIndent("   ")}
            |}""".trimMargin()
    }

    private class Drain(
        private val delegate: Iterator<Game>,
        private var remaining: Int
    ) : Iterator<Game> {

        override fun hasNext(): Boolean {
            return delegate.hasNext() && remaining > 0
        }

        override fun next(): Game {
            if (remaining == 0) {
                throw NoSuchElementException()
            }
            remaining--
            return delegate.next()
        }
    }
}

fun GameGenerator.bounded(count: Int): BoundedGameGenerator {
    if (this is BoundedGameGenerator && this.count <= count) {
        return this
    }
    return BoundedGameGeneratorImpl(this, count)
}
