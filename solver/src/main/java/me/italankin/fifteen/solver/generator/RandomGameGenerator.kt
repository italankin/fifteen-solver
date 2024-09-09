package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.Game
import me.italankin.fifteen.game.Game.Scrambler
import me.italankin.fifteen.game.scrambler.ShuffleScrambler
import me.italankin.fifteen.solver.generator.RandomGameGenerator.Builder
import me.italankin.fifteen.solver.generator.RandomGameGenerator.Builder.MissingTile
import kotlin.random.Random as KotlinRandom

/**
 * Generates random games with specified parameters
 */
class RandomGameGenerator(
    private val factory: GameFactory,
    private val size: GameGenerator.Size,
    private val missingTile: MissingTile,
    private val scrambler: Scrambler,
    private val skipSolved: Boolean
) : GameGenerator {

    override fun iterator(): Iterator<Game> = Generator()

    override fun toString(): String {
        return "Random(${factory}, ${size}, ${missingTile}, ${scrambler})"
    }

    class Builder {
        private var factory: GameFactory = GameFactory.Classic
        private var size: GameGenerator.Size = GameGenerator.Size.SIZE_4X4
        private var missingTile: MissingTile = MissingTile.Default
        private var scrambler: Scrambler = ShuffleScrambler()
        private var skipSolved: Boolean = true

        fun factory(factory: GameFactory): Builder {
            return apply { this.factory = factory }
        }

        fun size(size: GameGenerator.Size): Builder {
            return apply { this.size = size }
        }

        fun missingTile(missingTile: MissingTile): Builder {
            return apply { this.missingTile = missingTile }
        }

        fun scrambler(scrambler: Scrambler): Builder {
            return apply { this.scrambler = scrambler }
        }

        /**
         * @param skipSolved if `true`, solved games will not be produced
         */
        fun skipSolved(skipSolved: Boolean): Builder {
            return apply { this.skipSolved = skipSolved }
        }

        fun generator(): RandomGameGenerator {
            return RandomGameGenerator(factory, size, missingTile, scrambler, skipSolved)
        }

        /**
         * Shortcut for `generator().bounded(count)`
         */
        fun bounded(count: Int): BoundedGameGenerator {
            return generator().bounded(count)
        }

        sealed class MissingTile {

            abstract fun get(size: GameGenerator.Size): Int

            object Default : MissingTile() {
                override fun get(size: GameGenerator.Size): Int = size.size
                override fun toString(): String = "Default"
            }

            class Random(private val random: KotlinRandom = KotlinRandom) : MissingTile() {
                override fun get(size: GameGenerator.Size): Int {
                    return 1 + random.nextInt(size.size)
                }

                override fun toString(): String = "Random"
            }

            class Number(private val number: Int) : MissingTile() {

                init {
                    if (number <= 0) throw IllegalArgumentException("invalid number=$number, must be > 0")
                }

                override fun get(size: GameGenerator.Size): Int = number

                override fun toString(): String = "Number($number)"
            }
        }
    }

    private inner class Generator : Iterator<Game> {

        private val generator: () -> Game = {
            val missingTile = missingTile.get(size)
            var game: Game
            do {
                game = factory.create(size, missingTile, scrambler)
            } while (skipSolved && game.isSolved)
            game
        }

        override fun hasNext(): Boolean = true

        override fun next(): Game = generator()
    }
}

fun randomGames(builder: Builder.() -> Unit): RandomGameGenerator {
    return randomGames().apply(builder).generator()
}

fun randomGames(): Builder {
    return Builder()
}
