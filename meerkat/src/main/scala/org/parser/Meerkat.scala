package org.parser

import org.meerkat.parsers.AbstractCPSParsers
import org.meerkat.Syntax.syn
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.graph.neo4j.Neo4jInput.Entity
import org.meerkat.graph.parseGraphFromAllPositions
import org.meerkat.input.{GraphxInput, Input}
import org.meerkat.parsers.{AbstractCPSParsers, Parsers}
import org.meerkat.parsers.Parsers.{Symbol, V, inE, outE, ε}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.test.TestGraphDatabaseFactory
import org.parser.shared.Edge

import java.io.File
import java.nio.file.Files
import scala.collection.JavaConverters.asScalaBufferConverter


object Meerkat {
  type L = String
  type N = String

  def parse(graph: Input[L, N], grammar: AbstractCPSParsers.AbstractSymbol[L, N, _, _]): Int = {
    parseGraphFromAllPositions(grammar, graph).length
  }

  def edgesToNeo4jGraph(fileName: String, edges: List[Edge],
                        nodesCount: Integer): Neo4jInput = {
    val dbPath = new File(f"temp_dbs/meerkat/$fileName")
    dbPath.mkdirs()
    val db = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath)
    val tx = db.beginTx()
    if (db.getAllRelationships.stream().count() == edges.size) {
      println("db already created")
      return new Neo4jInput(db)
    }
    println("creating db")
    val nodes = List.fill(nodesCount)(db.createNode)
    var cnt = 0
    edges.foreach { e =>
      nodes(e.getFrom).createRelationshipTo(nodes(e.getTo), () => e.getLabel)
      cnt += 1
      if (cnt % 10000 == 0)
        println(cnt)
    }
    tx.success()
    tx.close()
    db.beginTx()
    println("done creating db")
    new Neo4jInput(db)
  }


  //  def getGraph(file: String): Input[L, N] = {
  //    val triples = getTriples(file)
  //    val (edges, nodesCount) = triplesToEdges(triples)
  //    val graph = edgesToNeo4jGraph(edges, nodesCount)
  //    graph
  //  }
  private def sameGen(bs: List[(Symbol[L, N, _], Symbol[L, N, _])]): Symbol[L, N, _] =
    bs.map { case (ls, rs) => ls ~ syn(sameGen(bs) | ε) ~ rs } match {
      case x :: Nil => syn(ε | x)
      case x :: y :: xs => syn(xs.foldLeft(x | y)(_ | _))
    }

  def getGrammar1(): AbstractCPSParsers.AbstractSymbol[L, N, _, _] = {
    syn(sameGen(List(("subclassof-1", "subclassof"), ("type-1", "type"))))
  }

  def getGrammar2(): AbstractCPSParsers.AbstractSymbol[L, N, _, _] = {
    syn(sameGen(List(("subclassof-1", "subclassof"))) ~ "subclassof")
  }

  def getYagoGrammar() = {
    syn(V((e: Entity) => e.getProperty("id") == "40324616") ~
      (inE((e: Entity) => e.label() == "P92580544")).+ ~
      inE((e: Entity) => e.label() == "P13305537").+ ~
      inE((e: Entity) => e.label() == "P59561600") ~
      inE((e: Entity) => e.label() == "P74636308"))
  }


  //  val grammar = new AnyRef {
  //

  //    //    (v { it.properties["id"] == "40324616" } seq
  //    //      (inE { it.label == "P92580544" } seq inV()).some seq
  //    //      (inE { it.label == "P13305537" } seq inV()).some seq
  //    //      inE { it.label == "P59561600" } seq inV() seq
  //    //      inE { it.label == "P74636308" } seq inV()
  //    //      ) using { _ -> Unit }
  //    //
     // val yagoG =
  //    val test = syn(V((e: Entity) => e.id == 1930288))
  //  }


}
