package org.parser

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME
import org.parser.combinators.graph.StartState
import org.parser.neo4j.DefaultNeo4jGraph
import java.nio.file.Path
import kotlin.test.assertEquals

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
    fun rdfAll(){
        val expected = mapOf(
            "atom-primitive.owl" to Pair(15454, 122),
            "biomedical-mesure-primitive.owl" to Pair(15156, 2871),
            "foaf.rdf" to Pair(4118, 10),
            "generations.owl" to Pair(2164, 0),
            "people_pets.rdf" to Pair(9472, 37),
            "pizza.owl" to Pair(56195, 1262),
            "skos.rdf" to Pair(810, 1),
            "travel.owl" to Pair(2499, 63),
            "univ-bench.owl" to Pair(2540, 81),
            "wine.rdf" to Pair(66572, 133),
        )

        val filesLine = "atom-primitive.owl,biomedical-mesure-primitive.owl,foaf.rdf,generations.owl,people_pets.rdf,pizza.owl,skos.rdf,travel.owl,univ-bench.owl,wine.rdf"
        val files = filesLine.split(",")
        for(file in files) {
            val db = GParsers.createNeo4jDb(file).database(DEFAULT_DATABASE_NAME)
            val graph = GParsers.getGraph(file, db, GParsers::edgesToNeo4jGraph)
            val cnt = GParsers.parse(graph, GParsers.firstGrammar2())
           // println("File $file, first query: $cnt")
            val cnt2 = GParsers.parse(graph, GParsers.secondGrammar())
           // println("File $file, second query: $cnt2")
            assertEquals(expected.getValue(file).first, cnt)
            assertEquals(expected.getValue(file).second, cnt2)
        }

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
    fun cfpq(){
        val file = "go.csv"
        val neo4j = GParsers.createNeo4jDb(file)
        val db = neo4j.database(DEFAULT_DATABASE_NAME)
        val graph = CFPQCsvGraph.getGraph(file, db)
        val grammar = CFPQCsvGraph.firstGrammar()
        val cnt = GParsers.parse(graph, grammar)
        println(cnt)
        Thread.sleep(10000)
        neo4j.shutdown()
    }
}