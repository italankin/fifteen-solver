package me.italankin.fifteen.solver.reporter

import me.italankin.fifteen.solver.*
import me.italankin.fifteen.solver.reporter.SystemOutReporter.Field
import me.italankin.fifteen.solver.util.Table
import me.italankin.fifteen.solver.util.Table.Column
import me.italankin.fifteen.solver.util.toPrettyString
import me.italankin.fifteen.solver.util.toString
import java.math.RoundingMode

/**
 * Reports solve session to [System.out].
 *
 * @param columns columns to show in summary table
 * @param sortBy [Field] to sort summary table
 * @param compareBy compare solvers by [Field]s
 * @param printSolutions print every solution found
 */
@Suppress("UNCHECKED_CAST")
class SystemOutReporter(
    private val columns: List<Column<TableData>> = Columns.DEFAULT,
    private val sortBy: Field? = null,
    private val compareBy: List<Field> = emptyList(),
    private val printSolutions: Boolean = false
) : Reporter {

    override fun onSessionSolutionFound(session: Session, solution: Solver.Solution) {
        if (!printSolutions) {
            return
        }
        println("\rscramble:")
        println(solution.start.toString().prependIndent("  "))
        println("solver: ${solution.solver}")
        println("solution (${solution.solution.size}): ${solution.solution.joinToString()}")
        println("end state:")
        println(solution.end.toString().prependIndent("  "))
        println()
    }

    override fun onSessionSolutionError(session: Session, error: SolveException) {
        System.err.println("\rerror: ${error.message}")
        System.err.println("game:")
        System.err.println(error.game.javaClass.simpleName.prependIndent("  "))
        System.err.println(error.game.toPrettyString().prependIndent("    "))
        System.err.println("stacktrace:\n${error.stackTraceToString().prependIndent("  ")}")
    }

    override fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) {
        if (results.isEmpty()) {
            println("\rno games")
            return
        }

        val stats = results.stats()

        class TotalData(val name: String, val data: String)

        val totalTable = Table<TotalData>(
            Column(null) { it.name },
            Column(null, Column.Align.RIGHT) { it.data }
        ).apply {
            append(TotalData("Total", "${stats.global.total.success} (${stats.global.total.error} errors)"))
            append(TotalData("Total time", "${session.stats.totalTimeMs} ms"))
            append(TotalData("Max memory", "${session.stats.memoryMax shr 20} MB"))
            append(TotalData("Avg memory", "${session.stats.memoryAvg shr 20} MB"))
            append(TotalData("GC count", "${session.stats.gcCount}"))
            append(TotalData("GC time", "${session.stats.gcTimeMs} ms"))
        }
        println(totalTable.toString())

        val sortedEntries = if (sortBy != null) {
            stats.bySolver.entries.sortedBy { (_, stats) -> sortBy.invoke(stats) as Comparable<Any> }
        } else {
            stats.bySolver.entries
        }
        val solversTable = Table(columns)
        sortedEntries.forEach { (solver, stats) -> solversTable.append(TableData.SolverData(solver, stats)) }
        solversTable.appendSeparator()
        solversTable.append(TableData.Total(stats.global, session.stats.totalTimeMs))
        println(solversTable.toString())

        if (session.solvers.size == 1) {
            return
        }
        for (field in compareBy) {
            printCompareBy(stats, field)
        }
    }

    private fun printCompareBy(allStats: AllStats, field: Field) {
        println("Compare by ${field.name}:")
        val compareTable = Table.createCompareTable(allStats, mapper = field::invoke)
        println(compareTable)
    }

    interface Field {
        val name: String

        operator fun invoke(stats: Stats): Comparable<*>
    }

    enum class DefaultFields : Field {
        CPU_TIME {
            override fun invoke(stats: Stats): Comparable<*> = stats.time.total
        },
        NODES_EXPLORED {
            override fun invoke(stats: Stats): Comparable<*> = stats.total.nodesExplored
        },
        NODES_UNEXPLORED {
            override fun invoke(stats: Stats): Comparable<*> = stats.total.nodesUnexplored
        },
        NODES_TOTAL {
            override fun invoke(stats: Stats): Comparable<*> = stats.total.run { nodesExplored + nodesUnexplored }
        },
        SPEED {
            override fun invoke(stats: Stats): Comparable<*> = stats.total.avgSpeed
        },
        SEARCH_SPEED {
            override fun invoke(stats: Stats): Comparable<*> = stats.total.avgSearchSpeed
        },
        MIN_TIME {
            override fun invoke(stats: Stats): Comparable<*> = stats.time.min
        },
        MAX_TIME {
            override fun invoke(stats: Stats): Comparable<*> = stats.time.max
        },
        AVG_TIME {
            override fun invoke(stats: Stats): Comparable<*> = stats.time.avg
        },
        MIN_MOVES {
            override fun invoke(stats: Stats): Comparable<*> = stats.moves.min
        },
        MAX_MOVES {
            override fun invoke(stats: Stats): Comparable<*> = stats.moves.max
        },
        AVG_MOVES {
            override fun invoke(stats: Stats): Comparable<*> = stats.moves.avg
        },
    }

    object Columns {

        /**
         * [Solver]'s name
         */
        val SOLVER = Column<TableData>("Solver") {
            it.name
        }

        /**
         * Total CPU time
         */
        val CPU_TIME = Column<TableData>("CPU time (ms)", Column.Align.RIGHT) {
            it.stats.time.total.rawValue.toString(0)
        }

        /**
         * Total number of nodes, explored by algorithm
         */
        val NODES_EXPLORED = Column<TableData>("Nodes explored", Column.Align.RIGHT) {
            it.stats.total.nodesExplored.toString()
        }

        /**
         * Average number of nodes, explored by algorithm
         */
        val AVG_NODES_EXPLORED = Column<TableData>("Avg nodes explored", Column.Align.RIGHT) {
            it.stats.total.avgNodesExplored.toString()
        }

        /**
         * Total number of nodes, which were not visited by algorithm
         */
        val NODES_UNEXPLORED = Column<TableData>("Nodes unexplored", Column.Align.RIGHT) {
            it.stats.total.nodesUnexplored.toString()
        }

        /**
         * Average number of nodes, which were not visited by algorithm
         */
        val AVG_NODES_UNEXPLORED = Column<TableData>("Avg nodes unexplored", Column.Align.RIGHT) {
            it.stats.total.avgNodesUnexplored.toString()
        }

        /**
         * Solve speed in games per second
         */
        val GAMES_PER_SEC = Column<TableData>("Speed (games/s)", Column.Align.RIGHT) {
            when (it) {
                is TableData.SolverData -> (1000f / it.stats.time.avg.rawValue).toString(3)
                is TableData.Total -> (it.stats.total.success.toFloat() / it.totalTime * 1000).toString(3)
            }
        }

        /**
         * Average time required to solve a game
         */
        val AVG_SPEED_GAMES = Column<TableData>("Avg speed (ms/game)", Column.Align.RIGHT) {
            when (it) {
                is TableData.SolverData -> it.stats.total.avgSpeed.toString(3)
                is TableData.Total -> (it.totalTime.toFloat() / it.stats.total.success).toString(3)
            }
        }

        /**
         * Average speed of nodes exploration
         */
        val AVG_SPEED_NODES = Column<TableData>("Avg speed (nodes/ms)", Column.Align.RIGHT) {
            when (it) {
                is TableData.SolverData -> it.stats.total.avgSearchSpeed.toString(1)
                is TableData.Total -> {
                    it.stats.total.nodesExplored.divide(it.totalTime.toBigDecimal(), RoundingMode.HALF_UP)
                        .toPlainString()
                }
            }
        }

        /**
         * Minimum solve time
         */
        val TIME_MIN = Column<TableData>("Time (min, ms)", Column.Align.RIGHT) {
            it.stats.time.min.rawValue.toString(3)
        }

        /**
         * Maximum solve time
         */
        val TIME_MAX = Column<TableData>("Time (max, ms)", Column.Align.RIGHT) {
            it.stats.time.max.rawValue.toString(3)
        }

        /**
         * Average solve time
         */
        val TIME_AVG = Column<TableData>("Time (avg, ms)", Column.Align.RIGHT) {
            it.stats.time.avg.rawValue.toString(3)
        }

        /**
         * 95th percentile of solve time
         */
        val TIME_P95 = Column<TableData>("Time (p95, ms)", Column.Align.RIGHT) {
            it.stats.time.p95.rawValue.toString(1)
        }

        /**
         * Standard deviation of solve time
         */
        val TIME_STDDEV = Column<TableData>("Time (stddev, ms)", Column.Align.RIGHT) {
            it.stats.time.stddev.rawValue.toString(1)
        }

        /**
         * Minimum number of moves
         */
        val MOVES_MIN = Column<TableData>("Moves (min)", Column.Align.RIGHT) {
            it.stats.moves.min.toString()
        }

        /**
         * Maximum number of moves
         */
        val MOVES_MAX = Column<TableData>("Moves (max)", Column.Align.RIGHT) {
            it.stats.moves.max.toString()
        }

        /**
         * Average number of moves
         */
        val MOVES_AVG = Column<TableData>("Moves (avg)", Column.Align.RIGHT) {
            it.stats.moves.avg.toString(3)
        }

        /**
         * 95th percentile of moves
         */
        val MOVES_P95 = Column<TableData>("Moves (p95)", Column.Align.RIGHT) {
            it.stats.moves.p95.toString()
        }

        /**
         * Standard deviation of moves
         */
        val MOVES_STDDEV = Column<TableData>("Moves (stddev)", Column.Align.RIGHT) {
            it.stats.moves.stddev.toString(3)
        }

        /**
         * Default columns set
         */
        val DEFAULT = listOf(
            SOLVER,
            CPU_TIME,
            GAMES_PER_SEC,
            AVG_SPEED_GAMES,
            TIME_MIN,
            TIME_MAX,
            TIME_AVG,
            MOVES_MIN,
            MOVES_MAX,
            MOVES_AVG,
        )
    }

    sealed class TableData {

        internal abstract val name: String
        internal abstract val stats: Stats

        internal class SolverData(
            val solver: Solver,
            override val stats: Stats
        ) : TableData() {
            override val name: String = solver.toString()
        }

        internal class Total(
            override val stats: Stats,
            val totalTime: Long
        ) : TableData() {
            override val name: String = "Total"
        }
    }
}
