package me.italankin.fifteen.solver

import me.italankin.fifteen.solver.util.toString
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun List<Result<Solver.Solution>>.stats(): AllStats {
    val bySolver = groupBy { result ->
        if (result.isSuccess) {
            result.getOrThrow().solver
        } else {
            val se = result.exceptionOrNull() as SolveException
            se.solver
        }
    }
        .mapValues { (_, results) -> results.calculateStats() }
    return AllStats(
        global = calculateStats(),
        bySolver = bySolver
    )
}

class AllStats(
    /**
     * Stats across all solvers
     */
    val global: Stats,
    /**
     * Stats for individual solvers
     */
    val bySolver: Map<Solver, Stats>
)

class Stats(
    val total: Total,
    val time: Time,
    val moves: Moves
) {

    class Total(
        val success: Int,
        val error: Int,
        val avgSpeed: Float,
        val avgSearchSpeed: Float,
        val nodesExplored: BigDecimal,
        val avgNodesExplored: BigDecimal,
        val nodesUnexplored: BigDecimal,
        val avgNodesUnexplored: BigDecimal
    )

    class Time(
        val total: Ms,
        val min: Ms,
        val max: Ms,
        val avg: Ms,
        val stddev: Ms,
        val p95: Ms
    )

    class Moves(
        val min: Long,
        val max: Long,
        val avg: Float,
        val stddev: Float,
        val p95: Int
    )
}

class Ms(val rawValue: Float) : Number(), Comparable<Ms> {
    override fun compareTo(other: Ms): Int = rawValue.compareTo(other.rawValue)
    override fun toString(): String = "${rawValue.toString(3)} ms"
    override fun toByte(): Byte = rawValue.toInt().toByte()
    override fun toDouble(): Double = rawValue.toDouble()
    override fun toFloat(): Float = rawValue
    override fun toInt(): Int = rawValue.toInt()
    override fun toLong(): Long = rawValue.toLong()
    override fun toShort(): Short = rawValue.toInt().toShort()
}

private fun List<Result<Solver.Solution>>.calculateStats(): Stats {
    val (success, error) = partitionResults()
    val totalNodesExplored = success.sumOf { it.nodesExplored.toBigDecimal() }
    val avgSearchSpeed = if (success.isNotEmpty()) {
        val totalTime = success.sumOf { it.time.toBigDecimal() }
        totalNodesExplored.divide(totalTime, MathContext.DECIMAL32).toFloat()
    } else {
        Float.NaN
    }
    val totalTime = success.sumOf { it.time.toDouble() }.toFloat()
    val avgSpeed = totalTime / size
    val totalNodesUnexplored = success.sumOf { it.nodesUnexplored.toBigDecimal() }
    val successSize = BigDecimal(success.size)
    val total = Stats.Total(
        success = success.size,
        error = error.size,
        avgSpeed = avgSpeed,
        avgSearchSpeed = avgSearchSpeed,
        nodesExplored = totalNodesExplored,
        avgNodesExplored = if (successSize == BigDecimal.ZERO) {
            BigDecimal.ZERO
        } else {
            totalNodesExplored.divide(successSize, RoundingMode.HALF_UP)
        },
        nodesUnexplored = totalNodesUnexplored,
        avgNodesUnexplored = if (successSize == BigDecimal.ZERO) {
            BigDecimal.ZERO
        } else {
            totalNodesUnexplored.divide(successSize, RoundingMode.HALF_UP)
        }
    )
    val time = success.time().let { v ->
        Stats.Time(
            total = Ms(v.total),
            min = Ms(v.min),
            max = Ms(v.max),
            avg = Ms(v.avg),
            stddev = Ms(v.stddev),
            Ms(v.p95)
        )
    }
    val moves = success.moves().let { v ->
        Stats.Moves(
            min = v.min.toLong(),
            max = v.max.toLong(),
            avg = v.avg,
            stddev = v.stddev,
            p95 = v.p95.toInt()
        )
    }
    return Stats(total, time, moves)
}

private fun List<Result<Solver.Solution>>.partitionResults(): Pair<List<Solver.Solution>, List<SolveException>> {
    val (success, error) = partition { it.isSuccess }
    val solutions = success.map(Result<Solver.Solution>::getOrThrow)
    val errors = error.map { it.exceptionOrNull() as SolveException }
    return solutions to errors
}

private class Value(
    val total: Float,
    val min: Float,
    val max: Float,
    val avg: Float,
    val stddev: Float,
    val p95: Float
)

private fun List<Solver.Solution>.time(): Value {
    if (isEmpty()) {
        return Value(Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN)
    }
    var min = Float.MAX_VALUE
    var max = 0f
    var sum = 0f
    forEach { s ->
        sum += s.time
        if (s.time < min) {
            min = s.time
        }
        if (s.time > max) {
            max = s.time
        }
    }
    val avg = sum / size
    val stddev = stddev(avg) { it.time }
    val perc95 = percentile(0.95f) { it.time }
    return Value(sum, min, max, avg, stddev, perc95)
}

private fun List<Solver.Solution>.moves(): Value {
    if (isEmpty()) {
        return Value(Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN)
    }
    var min = Int.MAX_VALUE
    var max = 0
    var sum = 0
    forEach { s ->
        sum += s.moves
        if (s.moves < min) {
            min = s.moves
        }
        if (s.moves > max) {
            max = s.moves
        }
    }
    val avg = sum.toFloat() / size
    val stddev = stddev(avg) { it.moves.toFloat() }
    val perc95 = percentile(.95f) { it.moves.toFloat() }
    return Value(sum.toFloat(), min.toFloat(), max.toFloat(), avg, stddev, perc95)
}

private fun List<Solver.Solution>.stddev(avg: Float, by: (Solver.Solution) -> Float): Float {
    if (size < 2) {
        return Float.NaN
    }
    val squaredDiffWithAvg = sumOf {
        val v = by(it) - avg
        (v * v).toDouble()
    }
    return sqrt(squaredDiffWithAvg.toFloat() / (size - 1))
}

/**
 * Calculate percentile from [Solver.Solution] data.
 *
 * @param p percentile value (`(0; 1]`)
 * @param by selector for values
 *
 * @return [p]th percentile or [Float.NaN], if list is empty
 */
fun List<Solver.Solution>.percentile(p: Float, by: (Solver.Solution) -> Float): Float {
    if (p <= 0 || p > 1) {
        throw IllegalArgumentException("p must be in range (0; 1]")
    }
    if (isEmpty()) return Float.NaN
    if (size == 1) return by(first())
    val values = map(by).sorted()
    if (p == 1f) return values.last()
    val nth = ceil((size - 1) * p).roundToInt()
    return values[nth]
}
