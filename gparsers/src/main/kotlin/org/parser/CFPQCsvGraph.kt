package org.parser

import org.neo4j.graphdb.GraphDatabaseService
import org.parser.combinators.*
import org.parser.neo4j.DefaultNeo4jCombinators.inE
import org.parser.neo4j.DefaultNeo4jCombinators.inV
import org.parser.neo4j.DefaultNeo4jCombinators.outE
import org.parser.neo4j.DefaultNeo4jCombinators.outV
import org.parser.neo4j.DefaultNeo4jCombinators.throughInE
import org.parser.neo4j.DefaultNeo4jCombinators.throughOutE
import org.parser.neo4j.DefaultNeo4jGraph
import org.parser.neo4j.DefaultNeo4jVertexState
import org.parser.shared.loadCsvEdges
import java.io.InputStream

object CFPQCsvGraph {
    private fun getFileStream(file: String): InputStream {
        return this.javaClass.getResourceAsStream("/cfpq/$file")
            ?: throw RuntimeException("Can't find cfpq resource $file")
    }

    fun getGraph(file: String, db: GraphDatabaseService): DefaultNeo4jGraph {
        val edges = loadCsvEdges(getFileStream(file)).toList()
        val nodesCount = edges.flatMap { listOf(it.from, it.to) }.toSet().size
        return GParsers.edgesToNeo4jGraph(db, edges, nodesCount)
    }

    fun firstGrammar(): BaseParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any> {
        val subclassof1 = throughInE { it.label == "subClassOf" }
        val subclassof = throughOutE { it.label == "subClassOf" }
        val type1 = throughInE { it.label == "type" }
        val type = throughOutE { it.label == "type" }
        val S = LazyParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any>()
        S.p =
                (subclassof1 seq (S or eps()) seq subclassof) or
                (type1 seq (S or eps()) seq type)

        return S
    }
}