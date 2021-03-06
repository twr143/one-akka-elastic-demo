package org.iv


import com.sksamuel.elastic4s.{ElasticClient, RequestFailure, RequestSuccess, Response}

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.delete.DeleteByQueryResponse
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import com.sksamuel.elastic4s.requests.update.UpdateByQueryResponse
import org.iv.model._
import org.iv.util._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by twr143 on 03.07.2021 at 7:23.
 */
class EmpDaoService(c: => ElasticClient, indexName: String)(implicit ec: ExecutionContext) {
  lazy val client = c

  def create(e: Employee): Future[String] = {
    client.execute {
      indexInto(indexName).fields(Materializer.toMap(e)).refreshImmediately
    }.collect(handleError.orElse({
      case results: RequestSuccess[IndexResponse] => results.result.result
    })).mapTo[String]

  }

  def deletebyQuery(q: String): Future[Long] = {
    client.execute {
      deleteIn(indexName).by(stringQuery(q)).refreshImmediately
    }.collect(handleError.orElse({
      case results: RequestSuccess[DeleteByQueryResponse] => results.result.deleted
    })).mapTo[Long]
  }

  def queryEmployees(q: String): Future[List[Employee]] = {
    client.execute {
      search(indexName).query(q)
    }.collect(handleError.orElse({
      case results: RequestSuccess[SearchResponse] => results.result.hits.hits.map(_.sourceAsMap)
        .toList.map(Materializer.fromMap[Employee])
    })).mapTo[List[Employee]]
  }

  def updateByQ(query: String, script: String): Future[Long] = {
    client.execute {
      updateByQuery(indexName, stringQuery(query)).script(script).refreshImmediately
    }.collect(handleError.orElse({
      case results: RequestSuccess[UpdateByQueryResponse] => results.result.updated
    })).mapTo[Long]
  }

  def terminateConnection() = client.close()

  private def handleError: PartialFunction[Response[_], Future[_]] = {
    case e: RequestFailure => throw new RuntimeException(e.error.reason)
  }
}
