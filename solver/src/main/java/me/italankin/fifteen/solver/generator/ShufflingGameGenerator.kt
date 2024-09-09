package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import kotlin.random.Random

/**
 * Drains games from [generator], shuffling them on each request.
 */
class ShufflingGameGenerator(
    private val generator: BoundedGameGenerator,
    private val random: Random = Random
) : BoundedGameGenerator by generator {

    override fun iterator(): Iterator<Game> = generator.shuffled(random).iterator()

    override fun toString(): String {
        return """Shuffling {
            |${generator.toString().prependIndent("   ")}
            |}""".trimMargin()
    }
}

/**
 * Shuffle games, produced by this generator
 */
fun BoundedGameGenerator.shuffle(random: Random = Random): BoundedGameGenerator {
    return ShufflingGameGenerator(this, random)
}
