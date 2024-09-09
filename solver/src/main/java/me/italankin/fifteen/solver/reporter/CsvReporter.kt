package me.italankin.fifteen.solver.reporter

import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.Solver
import java.io.File
import java.io.Writer
import kotlin.math.roundToInt

private const val SEPARATOR = ";"

/**
 * Write session data to CSV.
 *
 * @param output where to put data
 * @param columns list of columns to render
 * @param separator separator for columns
 */
class CsvReporter(
    private val output: Output,
    private val columns: List<Column>,
    private val separator: String = SEPARATOR
) : Reporter {

    init {
        if (columns.isEmpty()) {
            throw IllegalArgumentException("columns must not be empty")
        }
    }

    override fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) {
        output.writer().use { writer ->
            writer.write(columns.joinToString(separator, transform = Column::title))
            for ((index, result) in results.withIndex()) {
                val solution = result.getOrNull() ?: continue
                writer.write("\n")
                writer.write(columns.joinToString(separator, transform = { it.invoke(index, solution) }))
            }
        }
    }

    fun interface Output {

        fun writer(): Writer

        object ToSystemOut : Output {
            override fun writer(): Writer = System.out.writer()
        }

        class ToFile(private val file: File) : Output {
            init {
                if (file.exists()) {
                    throw IllegalStateException("$file exists")
                }
            }

            override fun writer(): Writer = file.bufferedWriter()
        }
    }

    interface Column {
        val title: String

        operator fun invoke(index: Int, solution: Solver.Solution): String
    }

    class TextColumn(override val title: String, private val value: String) : Column {
        override fun invoke(index: Int, solution: Solver.Solution): String = value
    }

    enum class DefaultColumns(override val title: String) : Column {
        INDEX("index") {
            override fun invoke(index: Int, solution: Solver.Solution): String = index.toString()
        },
        SOLVER("solver") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.solver.toString()
        },
        WIDTH("width") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.game.width.toString()
        },
        HEIGHT("height") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.game.height.toString()
        },
        SIZE("size") {
            override fun invoke(index: Int, solution: Solver.Solution): String {
                return "${solution.game.width}x${solution.game.height}"
            }
        },
        SCRAMBLE("scramble") {
            override fun invoke(index: Int, solution: Solver.Solution): String {
                return solution.game.state.joinToString(separator = ",")
            }
        },
        SOLUTION("solution") {
            override fun invoke(index: Int, solution: Solver.Solution): String {
                return solution.solution.joinToString(separator = ",")
            }
        },
        HEURISTICS("heuristics") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.solver.heuristics.toString()
        },
        HEURISTICS_VALUE("heuristics value") {
            override fun invoke(index: Int, solution: Solver.Solution): String {
                return solution.start.heuristicsValue.toString()
            }
        },
        ALGORITHM("algorithm") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.solver.algorithm.toString()
        },
        TIME("time") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.time.roundToInt().toString()
        },
        MOVES("moves") {
            override fun invoke(index: Int, solution: Solver.Solution): String = solution.moves.toString()
        },
    }
}
