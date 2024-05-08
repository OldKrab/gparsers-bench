package org.parser

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GparsersBenchTest {
    @Test
    fun test(){
        val db = GparsersBench.openInProcessNeo4jDb().defaultDatabaseService()
        val graph = GparsersBench.getGraph("atom-primitive.owl", db, GparsersBench::edgesToNeo4jGraph)
        val cnt = GparsersBench.parse(graph, GparsersBench.firstGrammar())
        println(cnt)
    }
}