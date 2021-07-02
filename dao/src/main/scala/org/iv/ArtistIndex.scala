package org.iv

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.SearchResponse

import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.fields.{DateField, TextField}
import org.iv.model._
import org.iv.util._

/**
 * Created by twr143 on 02.07.2021 at 8:33.
 */
object ArtistIndex extends App {


  // in this example we create a client to a local Docker container at localhost:9200
  val client = ElasticClient(JavaClient(ElasticProperties(s"http://${sys.env.getOrElse("ES_HOST", "127.0.0.1")}:${sys.env.getOrElse("ES_PORT", "9200")}")))

  // we must import the dsl


  val indexName = "learn2"
  // Next we create an index in advance ready to receive documents.
  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block
  // the calling thread but is useful when testing
  val em1 = Employee("ilya", "2021-07-03T12:11:13")
  val em2 = Employee("igor", "2021-04-03T12:11:13")
  val em3 = Employee("lev", "2021-04-03T12:11:13")
  client.execute(deleteIndex(indexName)).flatMap { _ =>
    client.execute {
      createIndex(indexName).mapping(
        properties(
          TextField("name"),
          DateField("joined")
        )
      )
    }
  }.await

  // Next we index a single document which is just the name of an Artist.
  // The RefreshPolicy.Immediate means that we want this document to flush to the disk immediately.
  // see the section on Eventual Consistency.
  client.execute {
    bulk(
      indexInto(indexName).fields(Materializer.toMap(em1)), //.refresh(RefreshPolicy.Immediate),
      indexInto(indexName).fields(Materializer.toMap(em2)), //.refresh(RefreshPolicy.Immediate),
      indexInto(indexName).fields(Materializer.toMap(em3)) //.refresh(RefreshPolicy.Immediate)
    ).refreshImmediately
  }.await

  client.execute {
    deleteIn(indexName).by(stringQuery("name:ig*")).refreshImmediately
  }.await


  // now we can search for the document we just indexed
  val resp = client.execute {
    search(indexName).query("name:i*")
  }.await

  // resp is a Response[+U] ADT consisting of either a RequestFailure containing the
  // Elasticsearch error details, or a RequestSuccess[U] that depends on the type of request.
  // In this case it is a RequestSuccess[SearchResponse]

  println("---- Search Results ----")
  resp match {
    case failure: RequestFailure => println("We failed " + failure.error)
    case results: RequestSuccess[SearchResponse] => println(results.result.hits.hits.map(_.sourceAsMap)
      .toList.map(Materializer.cmon[Employee]))
    case results: RequestSuccess[_] => println(results.result)
  }

  // Response also supports familiar combinators like map / flatMap / foreach:
  resp foreach (search => println(s"There were ${search.totalHits} total hits"))

  client.close()
}