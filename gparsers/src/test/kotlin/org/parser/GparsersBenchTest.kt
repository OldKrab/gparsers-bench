package org.parser

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.parser.combinators.graph.StartState
import org.parser.neo4j.DefaultNeo4jGraph
import java.nio.file.Path

class GparsersBenchTest {
    @Test
    @Disabled
    fun test(){
        val db = GparsersBench.openInProcessNeo4jDb().defaultDatabaseService()
        val graph = GparsersBench.getGraph("atom-primitive.owl", db, GparsersBench::edgesToNeo4jGraph)
        val cnt = GparsersBench.parse(graph, GparsersBench.firstGrammar())
        println(cnt)
    }

    @Test
    @Disabled
    fun yago(){
        var dbPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33"
        var dbConfigPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/conf/neo4j.conf"
        var dbName = "yago-neo4j"

        val neo4j = GparsersBench.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath))
        val graph = DefaultNeo4jGraph(neo4j.database(dbName))
        val res = GparsersBench.yagoReversedGrammar().parseState(StartState(graph)).map { it.rightState.v }
        println(res.joinToString("\n"))
    }
}