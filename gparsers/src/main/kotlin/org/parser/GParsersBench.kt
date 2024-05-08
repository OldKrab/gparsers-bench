package org.parser

import org.neo4j.harness.Neo4j
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.parser.neo4j.DefaultNeo4jGraph

@State(Scope.Benchmark)
open class GParsersBench {
    @Param(
        "atom-primitive.owl",
        "biomedical-mesure-primitive.owl",
        "foaf.rdf",
        //"funding.rdf",
        "generations.owl",
        "people_pets.rdf",
        "pizza.owl",
        "skos.rdf",
        "travel.owl",
        "univ-bench.owl",
        "wine.rdf"
    )
    var file = ""

    lateinit var graph: DefaultNeo4jGraph
    lateinit var neo4j: Neo4j

    @Setup
    fun setup(): Unit {
        neo4j = GparsersBench.openInProcessNeo4jDb()
        graph = GparsersBench.getGraph(file, neo4j.defaultDatabaseService(), GparsersBench::edgesToNeo4jGraph)
    }

    @TearDown
    fun tearDown() {
        neo4j.close()
    }

    @Benchmark
    fun firstQuery() {
        val cnt = GparsersBench.parse(graph, GparsersBench.firstGrammar())
    }


    @Benchmark
    fun secondQuery() {
        val cnt = GparsersBench.parse(graph, GparsersBench.secondGrammar())
    }
}

