package org.parser

import org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME
import org.neo4j.dbms.api.DatabaseManagementService
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.parser.neo4j.DefaultNeo4jGraph

@State(Scope.Benchmark)
open class RDFGParsersBench {
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
    lateinit var neo4j: DatabaseManagementService

    @Setup
    fun setup(): Unit {
        neo4j = GParsers.createNeo4jDb(file)
        graph = GParsers.getGraph(file, neo4j.database(DEFAULT_DATABASE_NAME), GParsers::edgesToNeo4jGraph)
    }

    @TearDown
    fun tearDown() {
        neo4j.shutdown()
    }

    @Benchmark
    fun firstQuery(): Int {
        return GParsers.parse(graph, GParsers.firstGrammar2())
    }


    @Benchmark
    fun secondQuery(): Int {
        return GParsers.parse(graph, GParsers.secondGrammar())
    }
}

