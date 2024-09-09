package me.italankin.fifteen.solver.generator

import me.italankin.fifteen.game.ClassicGame
import me.italankin.fifteen.game.Game
import me.italankin.fifteen.game.SnakeGame
import me.italankin.fifteen.game.SpiralGame

interface GameFactory {

    fun create(size: GameGenerator.Size, missingTile: Int, scrambler: Game.Scrambler): Game

    fun create(size: GameGenerator.Size, state: List<Int>): Game

    object Classic : GameFactory {
        override fun create(size: GameGenerator.Size, missingTile: Int, scrambler: Game.Scrambler): Game {
            return ClassicGame(size.width, size.height, missingTile, scrambler)
        }

        override fun create(size: GameGenerator.Size, state: List<Int>): Game {
            return ClassicGame(size.width, size.height, state)
        }

        override fun toString(): String = "Classic"
    }

    object Snake : GameFactory {
        override fun create(size: GameGenerator.Size, missingTile: Int, scrambler: Game.Scrambler): Game {
            return SnakeGame(size.width, size.height, missingTile, scrambler)
        }

        override fun create(size: GameGenerator.Size, state: List<Int>): Game {
            return SnakeGame(size.width, size.height, state)
        }

        override fun toString(): String = "Snake"
    }

    object Spiral : GameFactory {
        override fun create(size: GameGenerator.Size, missingTile: Int, scrambler: Game.Scrambler): Game {
            return SpiralGame(size.width, size.height, missingTile, scrambler)
        }

        override fun create(size: GameGenerator.Size, state: List<Int>): Game {
            return SpiralGame(size.width, size.height, state)
        }

        override fun toString(): String = "Spiral"
    }
}
