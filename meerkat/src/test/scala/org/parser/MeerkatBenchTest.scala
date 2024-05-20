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
    val cnt = executeQuery(Meerkat.getYagoGrammar(), graph).size
    println(cnt)
  }
  test("test2") {
    val file =  "atom-primitive.owl"
    val graph = Neo4jUtils.getGraph(file, Meerkat.edgesToNeo4jGraph)
    val cnt = Meerkat.parse(graph, Meerkat.getGrammar1())
    println(cnt)
  }

  test("testCFPQ") {
    val file =  "go_hierarchy.csv"
    val graph = CFPQCsvGraph.getGraph(file)

//    val neo4j = Neo4jUtils.openNeo4jDb(
//      Path.of("/home/old/diploma/sources/benchmark/gparsers/temp_db/c647d581752f13da1532478669abe42f/data/databases/neo4j/"), Path.of(""))
//    val graph = new Neo4jInput(neo4j)
    val g = CFPQCsvGraph.grammar

    val cnt =  Meerkat.parse(graph, g)
    println(cnt)
  }

}
