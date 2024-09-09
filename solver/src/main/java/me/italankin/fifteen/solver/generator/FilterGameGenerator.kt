package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

class FilterGameGenerator(
    private val generator: GameGenerator,
    private val filter: (Game) -> Boolean
) : GameGenerator {

    override fun iterator(): Iterator<Game> {
        return FilterIterator(generator.iterator())
    }

    override fun toString(): String {
        return """Filter {
            |${generator.toString().prependIndent("   ")}
            |}""".trimMargin()
    }

    private inner class FilterIterator(
        private val delegate: Iterator<Game>
    ) : Iterator<Game> {

        private var game: Game? = null

        override fun hasNext(): Boolean {
            if (game != null) {
                return true
            }
            var game: Game
            do {
                if (!delegate.hasNext()) {
                    return false
                }
                game = delegate.next()
            } while (!filter(game))
            this.game = game
            return true
        }

        override fun next(): Game {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val game = game!!
            this.game = null
            return game
        }
    }
}

class BoundedFilterGameGenerator(
    private val generator: BoundedGameGenerator,
    filter: (Game) -> Boolean
) : BoundedGameGenerator {

    private val games by lazy { generator.filter(filter) }

    override val count: Int
        get() = games.size

    override fun iterator(): Iterator<Game> {
        return games.iterator()
    }

    override fun toString(): String {
        return """Filter {
            |${generator.toString().prependIndent("   ")}
            |}""".trimMargin()
    }
}

fun GameGenerator.filterGames(filter: (Game) -> Boolean): GameGenerator {
    return FilterGameGenerator(this, filter)
}

fun BoundedGameGenerator.filterGames(filter: (Game) -> Boolean): BoundedGameGenerator {
    return BoundedFilterGameGenerator(this, filter)
}
