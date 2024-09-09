@file:JvmName("Extensions")

package me.italankin.fifteen.solver.util

import me.italankin.fifteen.game.Game
import java.util.*

fun Game.toPrettyString(): String {
    val width = size.toString().length + 1
    val sb = StringBuilder()
    state.forEachIndexed { i, n ->
        if (i > 0 && i % this.width == 0) {
            sb.appendLine()
        }
        sb.append(String.format("%${width}d", n))
    }
    return sb.toString()
}

internal fun Float.toString(precision: Int): String {
    return toDouble().toString(precision)
}

internal fun Double.toString(precision: Int): String {
    return "%.${precision}f".format(Locale.ROOT, this)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}
