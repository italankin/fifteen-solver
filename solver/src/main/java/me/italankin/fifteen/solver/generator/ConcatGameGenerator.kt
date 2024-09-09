package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import java.util.*

/**
 * Concatenates [generators] into a single one
 */
class ConcatGameGenerator(
    private val generators: List<BoundedGameGenerator>
) : BoundedGameGenerator {

    constructor(vararg generators: BoundedGameGenerator) : this(generators.toList())

    override val count: Int by lazy { generators.sumOf(BoundedGameGenerator::count) }

    override fun iterator(): Iterator<Game> {
        if (count == 0) {
            return Collections.emptyIterator()
        }
        return ConcatIterator(generators)
    }

    override fun toString(): String {
        return """Concat {
            |${generators.joinToString("\n") { it.toString().prependIndent("   ") }}
            |}""".trimMargin()
    }

    private class ConcatIterator(generators: List<BoundedGameGenerator>) : Iterator<Game> {

        private val iterators = generators.mapTo(ArrayList(), GameGenerator::iterator)

        override fun hasNext(): Boolean {
            while (true) {
                val iterator = iterators.firstOrNull() ?: return false
                if (iterator.hasNext()) {
                    return true
                }
                iterators.removeFirst()
            }
        }

        override fun next(): Game {
            while (true) {
                val iterator = iterators.firstOrNull() ?: throw NoSuchElementException()
                val next = iterator.next()
                if (!iterator.hasNext()) {
                    iterators.removeFirst()
                }
                return next
            }
        }
    }
}

fun List<BoundedGameGenerator>.concat(): BoundedGameGenerator {
    return ConcatGameGenerator(this)
}

operator fun BoundedGameGenerator.plus(next: BoundedGameGenerator): BoundedGameGenerator {
    return ConcatGameGenerator(listOf(this, next))
}

fun concatGames(builder: ConcatBuilder.() -> Unit): BoundedGameGenerator {
    return ConcatBuilder().apply(builder).build()
}

class ConcatBuilder {

    private val generators: MutableList<BoundedGameGenerator> = ArrayList()

    fun add(generator: BoundedGameGenerator) {
        generators.add(generator)
    }

    operator fun BoundedGameGenerator.unaryPlus() {
        generators.add(this)
    }

    fun build(): ConcatGameGenerator {
        return ConcatGameGenerator(generators)
    }
}
