package org.parser

import org.meerkat.input.Input
import org.meerkat.parsers.executeQuery
import org.openjdk.jmh.annotations._


@State(Scope.Benchmark)
class CFPQMeerkatBench {
  @Param(Array(""))
  var file = ""

  var graph: Input[String, String] = _

  @Setup
  def setup(): Unit = {
    graph = CFPQCsvGraph.getGraph(file)
  }

  @Benchmark
  def firstGrammar(): Unit = {
    val cnt = Meerkat.parse(graph, CFPQCsvGraph.grammar)
    print(cnt)
  }

}
