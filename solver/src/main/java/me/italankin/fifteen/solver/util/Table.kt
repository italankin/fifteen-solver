package me.italankin.fifteen.solver.util

import me.italankin.fifteen.solver.AllStats
import me.italankin.fifteen.solver.Solver
import me.italankin.fifteen.solver.Stats
import kotlin.math.max

private const val COL_DELIM = "│"
private const val ROW_DELIM = "─"
private const val CELL_LEFT_PAD = " "
private const val CELL_RIGHT_PAD = " "
private val BORDER_TOP = listOf('┌', '┬', '┐')
private val BORDER_MID = listOf('├', '┼', '┤')
private val BORDER_BOT = listOf('└', '┴', '┘')

class Table<T>(
    private val columns: List<Column<in T>>
) {

    companion object {

        /**
         * @param allStats stats to build table from
         * @param reverseOrder sorting order of table rows
         * @param mapper function for calculation of data, must return [Comparable][Comparable] [Number][Number]
         */
        @JvmStatic
        @JvmOverloads
        fun createCompareTable(
            allStats: AllStats,
            reverseOrder: Boolean = false,
            mapper: (Stats) -> Comparable<*>
        ): Table<*> {
            class Entry(val name: String, val data: String, val diff: String, val diffPerc: String)

            val mapped = allStats.bySolver.entries
                .map { it.key to mapper(it.value) }
            var comparator = compareBy<Pair<Solver, Comparable<*>>> { it.second }
            if (reverseOrder) {
                comparator = comparator.reversed()
            }
            val sorted = mapped.sortedWith(comparator)
            val baseValue = sorted.first().second
            val data = sorted.map { (solver, data) ->
                val baseFloat: Float
                val dataFloat: Float
                when {
                    baseValue is Number && data is Number -> {
                        baseFloat = baseValue.toFloat()
                        dataFloat = data.toFloat()
                    }
                    else -> {
                        throw IllegalArgumentException(
                            "Unsupported values pair: $baseValue (${baseValue.javaClass}), $data (${data.javaClass})"
                        )
                    }
                }

                val diff = dataFloat - baseFloat
                val diffPerc = diff / baseFloat * 100f

                fun Float.fmt(): String {
                    val prefix = if (this > 0f) "+" else ""
                    return prefix + toString(3)
                }
                Entry(
                    name = solver.toString(),
                    data = dataFloat.toString(3),
                    diff = diff.fmt(),
                    diffPerc = diffPerc.fmt()
                )
            }
            val compareTable = Table(
                Column("Solver", content = Entry::name),
                Column("Base", Column.Align.RIGHT, Entry::data),
                Column("Diff", Column.Align.RIGHT, Entry::diff),
                Column("Diff (%)", Column.Align.RIGHT, Entry::diffPerc),
            )
            data.forEach(compareTable::append)
            return compareTable
        }
    }

    private val rows: ArrayList<Row> = ArrayList(4)
    private val cellWidths = ArrayList<Int>(columns.size)

    constructor(vararg columns: Column<in T>) : this(columns.toList())

    init {
        if (columns.isEmpty()) {
            throw IllegalArgumentException("columns must not be empty")
        }
        rows += Border(Border.Type.TOP)
        if (columns.any { it.header != null }) {
            rows += CellsRow(columns.map { column -> Cell(column.header.orEmpty(), column.align) })
            rows += Border(Border.Type.MID)
        }
        columns.mapTo(cellWidths) { it.header?.length ?: 0 }
    }

    fun append(data: T) {
        val cells = columns.mapIndexed { i, column ->
            val content = column.content(data)
            cellWidths[i] = max(cellWidths[i], content.length)
            Cell(content, column.align)
        }
        rows += CellsRow(cells)
    }

    fun appendSeparator() {
        rows += Border(Border.Type.MID)
    }

    override fun toString(): String {
        val last = rows.lastOrNull()
        if (last !is Border || last.type != Border.Type.BOT) {
            rows += Border(Border.Type.BOT)
        }
        val result = StringBuilder()
        rows.forEachIndexed { i, row ->
            row.render(result, cellWidths)
            if (i != rows.lastIndex) {
                result.appendLine()
            }
        }
        return result.toString()
    }

    private class CellsRow(val cells: List<Cell>) : Row {
        override fun render(sb: StringBuilder, cellWidths: ArrayList<Int>) {
            sb.append(COL_DELIM)
            cells.forEachIndexed { j, cell ->
                cell.appendTo(sb, cellWidths[j])
                sb.append(COL_DELIM)
            }
        }
    }

    private class Border(val type: Type) : Row {
        override fun render(sb: StringBuilder, cellWidths: ArrayList<Int>) {
            sb.append(makeBorder(cellWidths, type))
        }

        private fun makeBorder(cellWidths: List<Int>, type: Type): StringBuilder {
            val result = StringBuilder()
            result.append(type.chars[0])
            cellWidths.forEachIndexed { i, width ->
                result.append(ROW_DELIM.repeat(CELL_LEFT_PAD.length + width + CELL_RIGHT_PAD.length))
                if (i == cellWidths.lastIndex) {
                    result.append(type.chars[2])
                } else {
                    result.append(type.chars[1])
                }
            }
            return result
        }

        enum class Type(val chars: List<Char>) {
            TOP(BORDER_TOP), MID(BORDER_MID), BOT(BORDER_BOT)
        }
    }

    private interface Row {
        fun render(sb: StringBuilder, cellWidths: ArrayList<Int>)
    }

    private class Cell(val content: String, align: Column.Align = Column.Align.LEFT) {

        private val align = when (align) {
            Column.Align.LEFT -> "-"
            Column.Align.RIGHT -> ""
        }

        fun appendTo(sb: StringBuilder, contentWidth: Int) {
            val format = "$CELL_LEFT_PAD%${align}${contentWidth}s$CELL_RIGHT_PAD"
            sb.append(String.format(format, content.take(contentWidth)))
        }
    }

    class Column<T>(
        val header: String?,
        val align: Align = Align.LEFT,
        val content: (T) -> String
    ) {

        enum class Align {
            LEFT, RIGHT
        }
    }
}
