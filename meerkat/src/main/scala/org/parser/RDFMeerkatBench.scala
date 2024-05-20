package org.parser

import org.meerkat.input.Input
import org.meerkat.parsers.executeQuery
import org.openjdk.jmh.annotations.{Benchmark, Param, Scope, Setup, State}
import org.parser.Neo4jUtils


@State(Scope.Benchmark)
class RDFMeerkatBench {
  @Param(Array(
    "atom-primitive.owl",
    "biomedical-mesure-primitive.owl",
    "foaf.rdf",
    //  "funding.rdf",
    "generations.owl",
    "people_pets.rdf",
    "pizza.owl",
    "skos.rdf",
    "travel.owl",
    "univ-bench.owl",
    "wine.rdf"))
  var file = ""

  var graph: Input[String, String] = _

  @Setup
  def setup(): Unit = {

    graph = Neo4jUtils.getGraph(file, Meerkat.edgesToNeo4jGraph)
  }

  @Benchmark
  def firstQuery(): Int = {
    Meerkat.parse(graph, Meerkat.getGrammar1())
  }

  @Benchmark
  def secondQuery(): Int = {
    Meerkat.parse(graph, Meerkat.getGrammar2())
  }

}
