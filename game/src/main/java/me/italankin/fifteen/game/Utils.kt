@file:JvmName("Utils")

package me.italankin.fifteen.game

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Manhattan distance by coordinates
 */
fun manhattan(x0: Int, y0: Int, x1: Int, y1: Int): Int {
    return abs(x1 - x0) + abs(y1 - y0)
}

/**
 * Manhattan distance by indices
 */
fun manhattan(from: Int, to: Int, width: Int): Int {
    return manhattan(from % width, from / width, to % width, to / width)
}

/**
 * Euclidean distance by coordinates
 */
fun euclidean(x0: Float, y0: Float, x1: Float, y1: Float): Float {
    val dx = x1 - x0
    val dy = y1 - y0
    return sqrt(dx * dx + dy * dy)
}

/**
 * Euclidean distance by coordinates
 */
fun euclidean(x0: Int, y0: Int, x1: Int, y1: Int): Float {
    return euclidean(x0.toFloat(), y0.toFloat(), x1.toFloat(), y1.toFloat())
}

/**
 * @return number of inversions in [list]
 */
fun inversions(list: List<Int>): Int {
    return inversions(list.toIntArray())
}

/**
 * @return number of inversions in [array]
 */
fun inversions(array: IntArray): Int {
    var inversions = 0
    val size = array.size
    // for every number we need to count:
    // - numbers less than chosen
    // - follow chosen number (by rows)
    for (i in 0 until size) {
        val n = array[i]
        if (n <= 1) {
            continue
        }
        for (j in i + 1 until size) {
            val m = array[j]
            if (m > 0 && n > m) {
                inversions++
            }
        }
    }
    return inversions
}
