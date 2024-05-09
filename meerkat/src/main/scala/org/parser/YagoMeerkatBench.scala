package org.parser

import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.input.Input
import org.meerkat.parsers.executeQuery
import org.neo4j.dbms.api.DatabaseManagementService
import org.neo4j.graphdb.GraphDatabaseService
import org.openjdk.jmh.annotations._

import java.nio.file.Path


@State(Scope.Benchmark)
class YagoMeerkatBench {
  @Param(Array(""))
  var dbPath = ""

  @Param(Array(""))
  var dbConfigPath = ""

  @Param(Array(""))
  var dbName = ""

  var graph: Input[String, String] = _
   var neo4j: DatabaseManagementService = _

  @Setup
  def setup(): Unit = {
    if(dbPath == "") throw new RuntimeException("No dbPath")
    if(dbConfigPath == "") throw new RuntimeException("No dbConfigPath")
    if(dbName == "") throw new RuntimeException("No dbName")
    neo4j = Neo4jUtils.openNeo4jDb(Path.of(dbPath), Path.of(dbConfigPath))
    val db = neo4j.database(dbName)
    graph = new Neo4jInput(db)
  }

  @TearDown
  def tearDown(): Unit = {
    neo4j.shutdown()
  }

  @Benchmark
  def yagoGrammar(): Unit = {
    val cnt = executeQuery(Meerkat.grammar.yagoG, graph).size
    println(cnt)
  }
}
