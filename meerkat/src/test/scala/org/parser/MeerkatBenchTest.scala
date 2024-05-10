package org.parser

import org.junit.Ignore
import org.junit.runner.RunWith
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.parsers.executeQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

import java.nio.file.Path
@Ignore
@RunWith(classOf[JUnitRunner])
class MeerkatBenchTest extends AnyFunSuite {
 test("test") {
    var dbPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/"
    var dbConfigPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/conf/neo4j.conf"
    var dbName = ""
    val neo4j = Neo4jUtils.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath))
    println(neo4j.getAllNodes.stream().count())

    val graph = new Neo4jInput(neo4j)
    val cnt = executeQuery(Meerkat.grammar.yagoG, graph).size
    println(cnt)
  }
  test("test2") {
    val file =  "atom-primitive.owl"
    val graph = Neo4jUtils.getGraph(file, Meerkat.edgesToNeo4jGraph)
    val cnt = executeQuery(Meerkat.grammar.test, graph).size
    println(cnt)
  }

  test("testCFPQ") {
    val file =  "eclass.csv"
    val graph = CFPQCsvGraph.getGraph(file)
    val cnt =  Meerkat.parse(graph, CFPQCsvGraph.grammar)
    println(cnt)
  }

}
