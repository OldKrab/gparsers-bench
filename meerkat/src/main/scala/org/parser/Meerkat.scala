package org.parser

import org.meerkat.parsers.AbstractCPSParsers
import org.meerkat.Syntax.syn
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.graph.neo4j.Neo4jInput.Entity
import org.meerkat.graph.parseGraphFromAllPositions
import org.meerkat.input.{GraphxInput, Input}
import org.meerkat.parsers.{AbstractCPSParsers, Parsers}
import org.meerkat.parsers.Parsers.{Symbol, V, inE, outE, ε}
import org.neo4j.test.TestGraphDatabaseFactory

import scala.collection.JavaConverters.asScalaBufferConverter


object Meerkat {
  type L = String
  type N = String

  def parse(graph: Input[L, N], grammar: AbstractCPSParsers.AbstractSymbol[L, N, _, _]): Int = {
    parseGraphFromAllPositions(grammar, graph).length
  }

  def edgesToNeo4jGraph(edges: java.util.List[Edge],
                        nodesCount: Integer): Neo4jInput = {
    val db = new TestGraphDatabaseFactory().newImpermanentDatabase
    db.beginTx()
    val nodes = List.fill(nodesCount)(db.createNode)
    edges.asScala.foreach { e =>
        val r = nodes(e.from).createRelationshipTo(nodes(e.to), () => e.label)
      r.setProperty("id", "42")
    }
    new Neo4jInput(db)
  }


//  def getGraph(file: String): Input[L, N] = {
//    val triples = getTriples(file)
//    val (edges, nodesCount) = triplesToEdges(triples)
//    val graph = edgesToNeo4jGraph(edges, nodesCount)
//    graph
//  }

  def getGrammar1(): AbstractCPSParsers.AbstractSymbol[L, N, _, _] = {
    grammar.G1
  }

  val grammar = new AnyRef {
    private def sameGen(
                         bs: List[(Symbol[L, N, _], Symbol[L, N, _])]): Symbol[L, N, _] =
      bs.map { case (ls, rs) => ls ~ syn(sameGen(bs) | ε) ~ rs } match {
        case x :: Nil     => syn(ε | x)
        case x :: y :: xs => syn(xs.foldLeft(x | y)(_ | _))
      }

    val G1 =
      syn(sameGen(List(("subclassof-1", "subclassof"), ("type-1", "type"))))

    val G2 =
      syn(sameGen(List(("subclassof-1", "subclassof"))) ~ "subclassof")

//    (v { it.properties["id"] == "40324616" } seq
//      (inE { it.label == "P92580544" } seq inV()).some seq
//      (inE { it.label == "P13305537" } seq inV()).some seq
//      inE { it.label == "P59561600" } seq inV() seq
//      inE { it.label == "P74636308" } seq inV()
//      ) using { _ -> Unit }
//
    val yagoG =  syn(V((e: Entity) => e.getProperty("id") == "40324616") ~
      inE((e: Entity) => e.label() ==  "P92580544").+ ~
      inE((e: Entity) => e.label() ==  "P13305537").+ ~
      inE((e: Entity) => e.label() ==  "P59561600") ~
      inE((e: Entity) => e.label() ==  "P74636308"))
    val test =  syn(V((e: Entity) => true) ~
      inE((e: Entity) => e.getProperty("id") == "40324616"))
  }


}
