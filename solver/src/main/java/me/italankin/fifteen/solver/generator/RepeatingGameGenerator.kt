package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

/**
 * Repeat results from [generator] specified number times
 */
class RepeatingGameGenerator(
    private val generator: BoundedGameGenerator,
    private val repeatCount: Int
) : BoundedGameGenerator {

    init {
        if (repeatCount < 0) {
            throw IllegalArgumentException("repeatCount must be >= 0")
        }
    }

    override val count: Int = generator.count * repeatCount

    override fun iterator(): Iterator<Game> = RepeatingIterator(generator, repeatCount)

    override fun toString(): String {
        return """Repeating(repeatCount=$repeatCount) {
            |${generator.toString().prependIndent("   ")}
            |}""".trimMargin()
    }

    class RepeatingIterator(
        private val generator: BoundedGameGenerator,
        repeatCount: Int
    ) : Iterator<Game> {

        private var repeatCount: Int = repeatCount - 1
        private var iterator: Iterator<Game> = generator.iterator()

        override fun hasNext(): Boolean {
            while (repeatCount >= 0) {
                if (iterator.hasNext()) {
                    return true
                }
                repeatCount--
                iterator = generator.iterator()
            }
            return false
        }

        override fun next(): Game {
            while (repeatCount >= 0) {
                val next = iterator.next()
                if (!iterator.hasNext()) {
                    repeatCount--
                    iterator = generator.iterator()
                }
                return next
            }
            throw NoSuchElementException()
        }
    }
}

fun BoundedGameGenerator.repeat(count: Int): BoundedGameGenerator {
    if (count == 1) {
        return this
    }
    return RepeatingGameGenerator(this, count)
}
