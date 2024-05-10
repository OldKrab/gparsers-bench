package org.parser.shared

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.reader


data class Edge(val from: Int, val label: String, val to: Int)

fun loadCsvEdges(csvStream: InputStream): Iterable<Edge> {
    // file has edges rows: tail of the edge, head of the edge, label of the edge
    return csvStream.bufferedReader().lineSequence()
        .map { it.split(" ") }
        .onEach { assert(it.size == 3) }
        .map { Edge(it[0].toInt(), it[2], it[1].toInt()) }
        .asIterable()
}