package org.iv


import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, RequestFailure, RequestSuccess, Response}
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.delete.DeleteByQueryResponse
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import org.iv.model._
import org.iv.util._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by twr143 on 03.07.2021 at 7:23.
 */
class EmpDaoService(implicit ec: ExecutionContext) {
  lazy val client = ElasticClient(JavaClient(ElasticProperties(s"http://${sys.env.getOrElse("ES_HOST", "127.0.0.1")}:${sys.env.getOrElse("ES_PORT", "9200")}")))
  val indexName = "learn2"

  def insert(e: Employee): Future[String] = {
    client.execute {
      indexInto(indexName).fields(Materializer.toMap(e)).refreshImmediately
    }.collect {
      case e: RequestFailure => throw new RuntimeException(e.error.reason)
      case results: RequestSuccess[IndexResponse] => results.result.result
    }

  }

  def deletebyQuery(q: String): Future[Long] = {
    client.execute {
      deleteIn(indexName).by(stringQuery(q)).refreshImmediately
    }.collect {
      case e: RequestFailure => throw new RuntimeException(e.error.reason) //s"failure at delete by $q ${failure.error}"
      case results: RequestSuccess[DeleteByQueryResponse] => results.result.deleted
    }
  }

  def queryEmployees(q: String): Future[List[Employee]] = {
    client.execute {
      search(indexName).query(q)
    }.collect(handleError("").orElse({
      case results: RequestSuccess[SearchResponse] => results.result.hits.hits.map(_.sourceAsMap)
        .toList.map(Materializer.cmon[Employee])
    })).mapTo[List[Employee]]
  }

  def updateByQuery(q: String, script: String): Future[Long] = {
    throw new UnsupportedOperationException("dao updateByQuery not supported yet")
  }

  def terminateConnection() = client.close()

  private def handleError(msg: String): PartialFunction[Response[_], Future[_]] = {
    case e: RequestFailure => throw new RuntimeException(e.error.reason)
  }
}
