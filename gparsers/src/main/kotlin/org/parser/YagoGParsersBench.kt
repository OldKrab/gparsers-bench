package org.parser

import org.neo4j.dbms.api.DatabaseManagementService
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.parser.combinators.graph.StartState
import org.parser.neo4j.DefaultNeo4jGraph
import java.nio.file.Path

@State(Scope.Benchmark)
open class YagoGParsersBench {
    @Param("")
    var dbPath = ""

    @Param("")
    var dbConfigPath = ""

    @Param("")
    var dbName = ""

    lateinit var graph: DefaultNeo4jGraph
    lateinit var neo4j: DatabaseManagementService

    @Setup
    fun setup(): Unit {
        if(dbPath == "") throw RuntimeException("No dbPath")
        if(dbConfigPath == "") throw RuntimeException("No dbConfigPath")
        if(dbName == "") throw RuntimeException("No dbName")

        neo4j = GParsers.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath))
        graph = DefaultNeo4jGraph(neo4j.database(dbName))
    }

    @TearDown
    fun tearDown() {
        neo4j.shutdown()
    }

    @Benchmark
    fun yagoGrammar(): Int {
        val res = GParsers.yagoReversedGrammar().parseState(StartState(graph))
        val cnt = res.size
        print(cnt)
        return cnt
    }

}

