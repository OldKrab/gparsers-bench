package org.parser

import org.meerkat.Syntax.syn
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.graph.neo4j.Neo4jInput.Entity
import org.meerkat.parsers.Parsers.{inE, outE, ε}
import org.meerkat.parsers.Parsers.{Symbol, V, inE, outE, ε}
import org.neo4j.graphdb.GraphDatabaseService
import org.parser.Meerkat.{L, N}
import org.parser.shared.CsvLoaderKt
import org.meerkat.parsers.AbstractCPSParsers
import org.meerkat.Syntax.syn
import org.meerkat.graph.neo4j.Neo4jInput
import org.meerkat.graph.neo4j.Neo4jInput.Entity
import org.meerkat.graph.parseGraphFromAllPositions
import org.meerkat.input.{GraphxInput, Input}
import org.meerkat.parsers.{AbstractCPSParsers, Parsers}
import org.meerkat.parsers.Parsers.{Symbol, V, inE, outE, ε}
import org.neo4j.test.TestGraphDatabaseFactory
import org.parser.shared.Edge

import scala.collection.JavaConverters.asScalaBufferConverter

import java.io.InputStream
import scala.collection.convert.ImplicitConversions.`iterable AsScalaIterable`

object CFPQCsvGraph {
  private def getFileStream(file: String): InputStream = {
    getClass.getResourceAsStream(f"/cfpq/$file")
  }

  def getGraph(file: String): Neo4jInput = {
    val edges = CsvLoaderKt.loadCsvEdges(getFileStream(file)).toList
    val nodesCount = edges.flatMap { case it => List(it.getFrom, it.getTo) }.toSet.size
    Meerkat.edgesToNeo4jGraph(file, edges, nodesCount)
  }


  val subclassof1: Symbol[L, N, _] = inE("subClassOf")
  val subclassof: Symbol[L, N, _] = outE("subClassOf")
  val type1: Symbol[L, N, _] = inE("type")
  val _type: Symbol[L, N, _] = outE("type")

  val grammar: Symbol[L, N, _] = syn((subclassof1 ~ syn(grammar | ε) ~ subclassof) | (type1 ~ syn(grammar | ε) ~ _type))


}
