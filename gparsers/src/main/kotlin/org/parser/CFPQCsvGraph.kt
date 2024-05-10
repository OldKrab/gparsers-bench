package org.parser

import org.neo4j.graphdb.GraphDatabaseService
import org.parser.combinators.*
import org.parser.neo4j.DefaultNeo4jCombinators.inE
import org.parser.neo4j.DefaultNeo4jCombinators.inV
import org.parser.neo4j.DefaultNeo4jCombinators.outE
import org.parser.neo4j.DefaultNeo4jCombinators.outV
import org.parser.neo4j.DefaultNeo4jGraph
import org.parser.neo4j.DefaultVertexState
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

    fun firstGrammar(): Parser<DefaultVertexState, DefaultVertexState, Unit> {
        val subclassof1 = inE { it.label == "subClassOf" }
        val subclassof = outE { it.label == "subClassOf" }
        val type1 = inE { it.label == "type" }
        val type = outE { it.label == "type" }
        val p = fix { S: Parser<DefaultVertexState, DefaultVertexState, Unit> ->
            rule(
                (subclassof1 seq inV() seq (S or eps()) seq subclassof seq outV()),
                (type1 seq inV() seq (S or eps()) seq type seq outV())
            ) using { _ ->
                Unit
            }
        }
        return p
    }
}