package me.italankin.fifteen.solver.reporter

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.italankin.fifteen.solver.Session
import me.italankin.fifteen.solver.Solver
import java.io.File
import java.io.OutputStream

/**
 * Writes solve data in JSON:
 *
 * ```json
 * [
 *   {
 *     "solver": "Solver(heuristics=ManhattanDistance, algorithm=A*(Default))",
 *     "scramble": "0, 7, 8, 3, 1, 5, 2, 4, 6",
 *     "solution": [...],
 *     "timeMs": 103.081245,
 *     "nodesExplored": 372,
 *     "nodesUnexplored": 231
 *   }
 * ]
 * ```
 *
 * @param output where to put data
 * @param tags additional tags to write for every solve entry
 * @param prettify use pretty-printing
 *
 */
class JsonReporter(
    private val output: Output,
    private val tags: List<String>? = null,
    private val prettify: Boolean = true,
) : Reporter {

    private val json = Json {
        prettyPrint = prettify
    }

    init {
        if (output is Output.ToFile && !output.append && output.file.exists())
            throw IllegalStateException("file '${output.file.absolutePath}' already exists")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onSessionFinished(session: Session, results: List<Result<Solver.Solution>>) {
        val data = results.mapNotNullTo(ArrayList()) {
            val solution = it.getOrNull() ?: return@mapNotNullTo null
            SolutionJson(
                solver = solution.solver.toString(),
                scramble = solution.game.state.joinToString(", "),
                solution = solution.solution,
                timeMs = solution.time,
                nodesExplored = solution.nodesExplored,
                nodesUnexplored = solution.nodesUnexplored,
                tags = tags
            )
        }
        if (output is Output.ToFile && output.append && output.file.exists()) {
            val existingData = output.file.inputStream()
                .use { json.decodeFromStream<List<SolutionJson>>(it) }
            data.addAll(0, existingData)
        }
        output.outputStream().use { json.encodeToStream(data, it) }
    }

    fun interface Output {

        fun outputStream(): OutputStream

        class ToFile(val file: File, val append: Boolean = false) : Output {
            constructor(path: String, append: Boolean = false) : this(File(path), append)

            override fun outputStream(): OutputStream = file.outputStream()
        }

        class ToStream(private val outputStreamProvider: () -> OutputStream) : Output {
            override fun outputStream(): OutputStream = outputStreamProvider()
        }
    }

    @Serializable
    data class SolutionJson(
        val solver: String,
        val scramble: String,
        val solution: List<Int>,
        val timeMs: Float,
        val nodesExplored: Long,
        val nodesUnexplored: Long,
        val tags: List<String>? = null
    )
}
