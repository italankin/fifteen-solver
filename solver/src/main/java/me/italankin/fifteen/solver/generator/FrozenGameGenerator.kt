package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game

/**
 * Freeze results from [generator], useful for reusing the same games for multiple
 * [me.italankin.fifteen.solver.Session]s.
 *
 * Produces the same result as `generator.toList().toGenerator()`, but adds more context for printing.
 */
class FrozenGameGenerator(
    private val generator: BoundedGameGenerator
) : BoundedGameGenerator by generator {

    private val games: List<Game> by lazy(generator::toList)

    override fun iterator(): Iterator<Game> = games.iterator()

    override fun toString(): String {
        return """Frozen {
              |${generator.toString().prependIndent("   ")}
              |}""".trimMargin()
    }
}

fun BoundedGameGenerator.freeze(): BoundedGameGenerator {
    if (this is FrozenGameGenerator) {
        return this
    }
    return FrozenGameGenerator(this)
}
