package org.parser

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.parser.combinators.graph.StartState
import org.parser.neo4j.DefaultNeo4jGraph
import java.nio.file.Path

class GParsersTest {
    @Test
    @Disabled
    fun test(){
        val db = GParsers.openInProcessNeo4jDb().defaultDatabaseService()
        val graph = GParsers.getGraph("atom-primitive.owl", db, GParsers::edgesToNeo4jGraph)
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
        val neo4j = GParsers.openInProcessNeo4jDb()
        val db = neo4j.defaultDatabaseService()
        val graph = CFPQCsvGraph.getGraph("eclass.csv", db)
        val cnt = GParsers.parse(graph, CFPQCsvGraph.firstGrammar())
        println(cnt)
        neo4j.close()

    }
}