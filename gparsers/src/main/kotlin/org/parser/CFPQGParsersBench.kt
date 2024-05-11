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
open class CFPQGParsersBench {
    @Param("")
    var file = ""


    lateinit var graph: DefaultNeo4jGraph
    lateinit var neo4j: DatabaseManagementService
    @Setup
    fun setup(): Unit {
        if(file == "") throw RuntimeException("No file")

        neo4j = GParsers.createNeo4jDb(file)
        graph = CFPQCsvGraph.getGraph(file, neo4j.database(DEFAULT_DATABASE_NAME))
    }

    @TearDown
    fun tearDown() {
        neo4j.shutdown()
    }

    @Benchmark
    fun firstQuery() {
        val cnt = GParsers.parse(graph, CFPQCsvGraph.firstGrammar())
        println(cnt)
    }

}

