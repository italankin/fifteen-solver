package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

/**
 * Produces games from a list
 */
class StaticGameGenerator(
    private val games: List<Game>
) : BoundedGameGenerator, Iterable<Game> by games {

    constructor(vararg games: Game) : this(games.toList())

    override val count: Int = games.size

    override fun toString(): String {
        return "Static(games=${games.size})"
    }
}

fun Game.toGenerator(): BoundedGameGenerator {
    return StaticGameGenerator(listOf(this))
}

fun List<Game>.toGenerator(): BoundedGameGenerator {
    return StaticGameGenerator(this)
}

fun List<Int>.toGame(
    factory: GameFactory = GameFactory.Classic,
    size: GameGenerator.Size = GameGenerator.Size.SIZE_4X4
): Game {
    return factory.create(size, this)
}

fun staticGames(configure: StaticBuilder.() -> Unit): StaticGameGenerator {
    return StaticBuilder().apply(configure).build()
}

class StaticBuilder {
    private val games: MutableList<Game> = ArrayList()

    fun add(game: Game) {
        games.add(game)
    }

    operator fun Game.unaryPlus() {
        games.add(this)
    }

    operator fun Iterable<Game>.unaryPlus() {
        games.addAll(this)
    }

    fun build(): StaticGameGenerator {
        return StaticGameGenerator(games)
    }
}
