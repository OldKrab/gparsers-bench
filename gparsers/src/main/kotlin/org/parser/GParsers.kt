package org.parser

import org.apache.jena.rdf.model.ModelFactory
import org.neo4j.configuration.GraphDatabaseSettings
import org.neo4j.dbms.api.DatabaseManagementService
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder
import org.neo4j.graphdb.GraphDatabaseService
import org.parser.combinators.*
import org.parser.combinators.graph.VertexState
import org.parser.neo4j.*
import org.parser.neo4j.DefaultNeo4jCombinators.outE
import org.parser.neo4j.DefaultNeo4jCombinators.inE
import org.parser.neo4j.DefaultNeo4jCombinators.inV
import org.parser.neo4j.DefaultNeo4jCombinators.outV
import org.parser.neo4j.DefaultNeo4jCombinators.throughInE
import org.parser.neo4j.DefaultNeo4jCombinators.throughOutE
import org.parser.neo4j.DefaultNeo4jCombinators.v
import org.parser.shared.Edge
import java.net.URI
import java.nio.file.Path
import java.util.*

object GParsers {
    fun openNeo4jDb(neo4jHome: Path, neo4jConfig: Path): DatabaseManagementService {
        return DatabaseManagementServiceBuilder(neo4jHome)
            .loadPropertiesFromFile(neo4jConfig).build()
    }


    fun createNeo4jDb(file: String): DatabaseManagementService {
        val dbPath = Path.of("temp_dbs/gparsers/$file")
        val managementService = DatabaseManagementServiceBuilder(dbPath).build();
        return managementService
    }

    fun edgesToNeo4jGraph(db: GraphDatabaseService, edges: List<Edge>, nodesCount: Int): DefaultNeo4jGraph {
        val tx = db.beginTx()
        if (tx.allRelationships.count() == edges.size) {
            println("db already filled")
            return DefaultNeo4jGraph(db)
        }
        println("creating db")
        val nodes = List(nodesCount) { tx.createNode() }
        edges.forEach { e ->
            nodes[e.from].createRelationshipTo(nodes[e.to]) { e.label }
        }
        tx.commit()
        return DefaultNeo4jGraph(db)
    }

    fun <T> parse(
        graph: DefaultNeo4jGraph,
        grammar: BaseParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, T>
    ): Int {
        val nodes = graph.getVertexes()
        var size = 0
        var cnt = 0
        for (node in nodes) {
            val nodes = grammar.parseState(VertexState(graph, node))
            val cur = nodes.size
            size += cur
            cnt += 1
//            if(cur != 0){
//                println("$cnt -> $cur")
//            }
//            if (cnt % 100000 == 0)
//                println("Parsed $cnt nodes")
        }
        return size
    }

    fun firstGrammar(): BaseParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any> {
        val subclassof1 = throughOutE { it.label == "subclassof-1" }
        val subclassof = throughOutE { it.label == "subclassof" }
        val type1 = throughOutE { it.label == "type-1" }
        val type = throughOutE { it.label == "type" }
        val S = LazyParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any>()
        S.p =
            (subclassof1 seq (S or eps()) seq subclassof) or
                    (type1 seq (S or eps()) seq type)

        return S
    }

    fun secondGrammar(): BaseParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any> {
        val subclassof1 = throughOutE { it.label == "subclassof-1" }
        val subclassof = throughOutE { it.label == "subclassof" }

        val S = LazyParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Any>()
        S.p = (subclassof1 seq (S or eps()) seq subclassof)

        return ((S or eps()) seq subclassof)
    }

    fun yagoGrammar(): BaseParser<DefaultNeo4jVertexState, DefaultNeo4jVertexState, Unit> {
        //MATCH (x)-[:P74636308]->()-[:P59561600]->()-[:P13305537*1..]->()-[:P92580544*1..]->(:Entity{id:'40324616'})
        // RETURN DISTINCT x

        return (outE { it.label == "P74636308" } seq
                outV() seq outE { it.label == "P59561600" } seq
                (outV() seq outE { it.label == "P13305537" }).many seq
                (outV() seq outE { it.label == "P92580544" }).many seq
                outV { it.id == 40324616L }
                ) using { _ -> Unit }
    }

    fun yagoReversedGrammar(): BaseParser<DefaultNeo4jStartState, DefaultNeo4jVertexState, Unit> {
        //MATCH (x)-[:P74636308]->()-[:P59561600]->()-[:P13305537*1..]->()-[:P92580544*1..]->(:Entity{id:'40324616'})
        // RETURN DISTINCT x

        return (v { it.properties["id"] == "40324616" } seq
                (inE { it.label == "P92580544" } seq inV()).some seq
                (inE { it.label == "P13305537" } seq inV()).some seq
                inE { it.label == "P59561600" } seq inV() seq
                inE { it.label == "P74636308" } seq inV()
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

    fun <T> getGraph(
        file: String,
        db: GraphDatabaseService,
        edgesToGraph: (GraphDatabaseService, List<Edge>, Int) -> T
    ): T {
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