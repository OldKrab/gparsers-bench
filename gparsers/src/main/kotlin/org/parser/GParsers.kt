package org.parser

import org.apache.jena.rdf.model.ModelFactory
import org.neo4j.dbms.api.DatabaseManagementService
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.parser.combinators.*
import org.parser.combinators.graph.VertexState
import org.parser.neo4j.*
import org.parser.neo4j.DefaultNeo4jCombinators.edge
import org.parser.neo4j.DefaultNeo4jCombinators.outV
import java.net.URI
import java.nio.file.Path
import java.util.*

data class Edge(val from: Int, val label: String, val to: Int)

object GparsersBench {
    fun openNeo4jDb(neo4jHome: Path, neo4jConfig: Path, dbName: String): DatabaseManagementService {
        return DatabaseManagementServiceBuilder(neo4jHome)
            .loadPropertiesFromFile(neo4jConfig).build()
    }


    fun openInProcessNeo4jDb(): Neo4j {
        val neo4j = Neo4jBuilders.newInProcessBuilder().build()
        val db = neo4j.defaultDatabaseService()
        return neo4j
    }

    fun edgesToNeo4jGraph(db: GraphDatabaseService, edges: List<Edge>, nodesCount: Int): DefaultNeo4jGraph {
        val tx = db.beginTx()
        val nodes = List(nodesCount) { tx.createNode() }
        edges.forEach { e ->
            nodes[e.from].createRelationshipTo(nodes[e.to]) { e.label }
        }
        tx.commit()
        return DefaultNeo4jGraph(db)
    }

    fun parse(
        graph: DefaultNeo4jGraph,
        grammar: Parser<DefaultVertexState, DefaultVertexState, Unit>
    ): Int {
        val nodes = graph.getVertexes()
        var size = 0
        var cnt = 0
        for (node in nodes) {
            val cur = grammar.parseState(VertexState(graph, node)).size
            size += cur
            cnt += 1
            if(cnt % 100000 == 0)
                println("Parsed $cnt nodes")
        }
        return size
    }


    fun firstGrammar(): Parser<DefaultVertexState, DefaultVertexState, Unit> {
        val subclassof1 = edge { it.label == "subclassof-1" }
        val subclassof = edge { it.label == "subclassof" }
        val type1 = edge { it.label == "type-1" }
        val type = edge { it.label == "type" }
        val p = fix { S: Parser<DefaultVertexState, DefaultVertexState, Unit> ->
            rule(
                (subclassof1 seq outV() seq (S or eps()) seq subclassof seq outV()) using { _ -> Unit },
                (type1 seq outV() seq (S or eps()) seq type seq outV()) using { _ -> Unit },
            )
        }
        return p
    }

    fun secondGrammar(): Parser<DefaultVertexState, DefaultVertexState, Unit> {
        val subclassof1 = edge { it.label == "subclassof-1" }
        val subclassof = edge { it.label == "subclassof" }
        val B = fix { B: Parser<DefaultVertexState, DefaultVertexState, Unit> ->
            rule(
                (subclassof1 seq outV() seq B seq subclassof seq outV()) using { _ -> Unit },
                (subclassof1 seq outV() seq subclassof seq outV()) using { _ -> Unit },
            )
        }
        return ((B or eps()) seq subclassof seq outV()) using { _ -> Unit }
    }

    fun yagoGrammar(): Parser<DefaultVertexState, DefaultVertexState, Unit> {
        //MATCH (x)-[:P74636308]->()-[:P59561600]->()-[:P13305537*1..]->()-[:P92580544*1..]->(:Entity{id:'40324616'})
        // RETURN DISTINCT x

        return (edge { it.label == "P74636308" } seq
                outV() seq edge { it.label == "P59561600" } seq
                (outV() seq edge { it.label == "P13305537" }).many seq
                (outV() seq edge { it.label == "P92580544" }).many seq
                outV { it.id == 40324616L }
                ) using { _ -> Unit }
    }

    private fun getTriples(file: String): List<Triple<String, String, String>> {
        val inputStream = this.javaClass.getResourceAsStream("/rdf/$file")
            ?: throw RuntimeException("Can't find rdf resource $file")
        val model = ModelFactory.createDefaultModel()
        model.read(inputStream, null)
        return model
            .listStatements()
            .toList()
            .map { stmt ->
                Triple(
                    stmt.getObject().toString(),
                    stmt.getPredicate().toString(),
                    stmt.getSubject().toString()
                )
            }
    }


    private fun triplesToEdges(triples: List<Triple<String, String, String>>)
            : Pair<List<Edge>, Int> {
        val nodes: Map<String, Int> =
            triples.flatMap { (f, _, t) -> listOf(f, t) }.toSet().sorted().withIndex()
                .associate { it.value to it.index }


        val edges = triples.flatMap { (f, l, t) ->
            val from = nodes[f]!!
            val to = nodes[t]!!
            val label = URI(l).fragment?.lowercase(Locale.getDefault())

            if (label == null) {
                listOf(Edge(from, "noLabel", to))
            } else if (label == "type") {
                listOf(Edge(from, "type", to), Edge(to, "type-1", from))
            } else if (label == "subclassof") {
                listOf(Edge(from, "subclassof", to), Edge(to, "subclassof-1", from))
            } else {
                listOf(Edge(from, label, to))
            }
        }
        return Pair(edges, nodes.size)
    }

    fun <T> getGraph(file: String, db: GraphDatabaseService, edgesToGraph: (GraphDatabaseService, List<Edge>, Int) -> T): T {
        val triples = getTriples(file)
        val (edges, nodesCount) = triplesToEdges(triples)
        val graph = edgesToGraph(db, edges, nodesCount)
        return graph
    }



    fun toDot(edges: List<Edge>) {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("digraph {") // Start of the DOT graph

        for ((from, label, to) in edges) {
            stringBuilder.appendLine("    \"$from\" -> \"$to\" [label=\"$label\"];")
        }
        stringBuilder.appendLine("}") // End of the DOT graph
        println(stringBuilder.toString())
    }
}