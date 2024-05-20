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




  test("rdfAll") {
    val expected = Map(
      "atom-primitive.owl" -> (15454, 122),
      "biomedical-mesure-primitive.owl" ->(15156, 2871),
      "foaf.rdf" -> (4118, 10),
      "generations.owl" -> (2164, 0),
      "people_pets.rdf" -> (9472, 37),
      "pizza.owl" -> (56195, 1262),
      "skos.rdf" -> (810, 1),
      "travel.owl"-> (2499, 63),
      "univ-bench.owl" -> (2540, 81),
      "wine.rdf" -> (66572, 133),
    )
    val filesLine = "atom-primitive.owl,biomedical-mesure-primitive.owl,foaf.rdf,generations.owl,people_pets.rdf,pizza.owl,skos.rdf,travel.owl,univ-bench.owl,wine.rdf"
    val files = filesLine.split(",")
    files.foreach(file => {
      val graph = Neo4jUtils.getGraph(file, Meerkat.edgesToNeo4jGraph)
      val cnt = Meerkat.parse(graph, Meerkat.getGrammar1())
     // println(f"File $file, first query: $cnt")
      val cnt2 = Meerkat.parse(graph, Meerkat.getGrammar2())
     // println(f"File $file, second query: $cnt2")
      assertResult(expected(file)._1)(cnt)
      assertResult(expected(file)._2)(cnt2)
    })

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


@Ignore
@RunWith(classOf[JUnitRunner])
class MeerkatRDFTest extends AnyFunSuite {
  test("rdfAll") {
    val expected = Map(
      "atom-primitive.owl" -> (15454, 122),
      "biomedical-mesure-primitive.owl" ->(15156, 2871),
      "foaf.rdf" -> (4118, 10),
      "generations.owl" -> (2164, 0),
      "people_pets.rdf" -> (9472, 37),
      "pizza.owl" -> (56195, 1262),
      "skos.rdf" -> (810, 1),
      "travel.owl"-> (2499, 63),
      "univ-bench.owl" -> (2540, 81),
      "wine.rdf" -> (66572, 133),
    )
    val filesLine = "atom-primitive.owl,biomedical-mesure-primitive.owl,foaf.rdf,generations.owl,people_pets.rdf,pizza.owl,skos.rdf,travel.owl,univ-bench.owl,wine.rdf"
    val files = filesLine.split(",")
    files.foreach(file => {
      val graph = Neo4jUtils.getGraph(file, Meerkat.edgesToNeo4jGraph)
      val cnt = Meerkat.parse(graph, Meerkat.getGrammar1())
      // println(f"File $file, first query: $cnt")
      val cnt2 = Meerkat.parse(graph, Meerkat.getGrammar2())
      // println(f"File $file, second query: $cnt2")
      assertResult(expected(file)._1)(cnt)
      assertResult(expected(file)._2)(cnt2)
    })
  }
}

@Ignore
@RunWith(classOf[JUnitRunner])
class MeerkatCFPQTest extends AnyFunSuite {
  test("cfpqAll") {
    val filesLine = "go_hierarchy.csv,eclass.csv,enzyme.csv,geospecies.csv,go.csv"
    val files = filesLine.split(",")
    files.foreach(file => {
      val graph = CFPQCsvGraph.getGraph(file)
      val g = CFPQCsvGraph.grammar
      val cnt =  Meerkat.parse(graph, g)
      println(f"File $file, query: $cnt")
    })
  }
}


