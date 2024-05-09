package org.parser

import org.junit.runner.RunWith
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.parsers.executeQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

import java.nio.file.Path

@RunWith(classOf[JUnitRunner])
class MeerkatBenchTest extends AnyFunSuite {
  test("test") {
    var dbPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/"
    var dbConfigPath = "/home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/conf/neo4j.conf"
    var dbName = ""
    val neo4j = Neo4jUtils.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath)).database("yago-neo4j")
    println(neo4j.beginTx().getAllNodes.stream().count())

    val graph = new Neo4jInput(neo4j)
    val cnt = executeQuery(Meerkat.grammar.test, graph).size
    println(cnt)
  }

}
