package me.italankin.fifteen.solver

/**
 * Current state of the solving queue
 *
 * @param inQueue games in queue
 * @param inProgress games currently solving
 * @param done solved games
 */
data class QueueState(
    val inQueue: Int,
    val inProgress: Int,
    val done: Int
)
