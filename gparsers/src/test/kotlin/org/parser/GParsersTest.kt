package org.parser

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME
import org.parser.combinators.graph.StartState
import org.parser.neo4j.DefaultNeo4jGraph
import java.nio.file.Path

class GParsersTest {
    @Test
    @Disabled
    fun test(){
        val file = "atom-primitive.owl"
        val db = GParsers.createNeo4jDb(file).database(DEFAULT_DATABASE_NAME)
        val graph = GParsers.getGraph(file, db, GParsers::edgesToNeo4jGraph)
        val cnt = GParsers.parse(graph, GParsers.firstGrammar())
        println(cnt)
    }

    @Test
    @Disabled
    fun yago(){
        var dbPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33"
        var dbConfigPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/conf/neo4j.conf"
        var dbName = "yago-neo4j"

        val neo4j = GParsers.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath))
        val graph = DefaultNeo4jGraph(neo4j.database(dbName))
        val res = GParsers.yagoReversedGrammar().parseState(StartState(graph)).map { it.rightState.v }
        println(res.joinToString("\n"))
    }

    @Test
    @Disabled
    fun cfpq(){
        val file = "enzyme.csv"
        val neo4j = GParsers.createNeo4jDb(file)
        val db = neo4j.database(DEFAULT_DATABASE_NAME)
        val graph = CFPQCsvGraph.getGraph(file, db)
        val cnt = GParsers.parse(graph, CFPQCsvGraph.firstGrammar())
        println(cnt)
        neo4j.shutdown()
    }
}