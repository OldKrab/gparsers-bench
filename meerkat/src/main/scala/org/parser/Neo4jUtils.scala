package org.parser

import org.apache.jena.rdf.model.ModelFactory

import java.net.URI
import scala.collection.JavaConverters.{asScalaIteratorConverter, seqAsJavaListConverter}

class Edge(val from: Int, val label: String, val to: Int)

object Neo4jUtils {
  def getTriples(file: String): List[(String, String, String)] = {
    val inputStream = getClass.getResourceAsStream(s"/rdf/$file")
    if(inputStream == null) throw new RuntimeException("Can't load resource")
    val model = ModelFactory.createDefaultModel
    model.read(inputStream, null)
    model
      .listStatements()
      .asScala
      .map { stmt =>
        (stmt.getObject.toString,
          stmt.getPredicate.toString,
          stmt.getSubject.toString)
      }
      .toList
  }

  def triplesToEdges(triples: List[(String, String, String)])
  : (List[Edge], Int) = {
    val nodes =
      triples.flatMap { case (f, _, t) => List(f, t) }.distinct.sorted.zipWithIndex.toMap
    val edges = triples.flatMap {
      case (f, l, t) =>
        val from = nodes(f)
        val to = nodes(t)
        val label = Option(new URI(l).getFragment).map(_.toLowerCase)
        label match {
          case Some("type") =>
            List(new Edge(from, "type", to), new Edge(to, "type-1", from))
          case Some("subclassof") =>
            List(new Edge(from, "subclassof", to), new Edge(to, "subclassof-1", from))
          case Some(lbl) => List(new Edge(from, lbl, to))
          case None => List(new Edge(from, "noLabel", to))
        }
    }
    (edges, nodes.size)
  }


  def getGraph[T](file: String, edgesToGraph: (java.util.List[Edge], Integer) => T): T = {
    val triples = getTriples(file)
    val (edges, nodesCount) = triplesToEdges(triples)
    val graph = edgesToGraph(edges.asJava, nodesCount)
    graph
  }

}

