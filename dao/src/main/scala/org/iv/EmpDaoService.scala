package org.iv

import com.sksamuel.elastic4s.ElasticDsl.{createIndex, deleteIndex, properties}
import com.sksamuel.elastic4s.fields.{DateField, TextField}
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
//  client.execute {
//      createIndex(indexName).mapping(
//        properties(
//          TextField("name"),
//          DateField("joined")
//        )
//      )
//  }

  def insert(e: Employee): Future[String] = {
    client.execute {
      indexInto(indexName).fields(Materializer.toMap(e)).refreshImmediately
    }.map { r =>
      r match {
        case failure: RequestFailure => throw new RuntimeException(s"failure at insert of $e ${failure.error}")
        case results: RequestSuccess[IndexResponse] => results.result.result
      }
    }

  }

  def deletebyQuery(q: String): Future[Long] = {
    client.execute {
      deleteIn(indexName).by(stringQuery(q)).refreshImmediately
    }.map { r =>
      r match {
        case e: RequestFailure => throw new RuntimeException(e.error.reason) //s"failure at delete by $q ${failure.error}"
        case results: RequestSuccess[DeleteByQueryResponse] => results.result.deleted
      }
    }
  }

  def queryEmployees(q: String): Future[List[Employee]] = {
    client.execute {
      search(indexName).query(q)
    }.map { r =>
      r match {
        case e: RequestFailure => throw new RuntimeException(e.error.reason) //s"failure at delete by $q ${failure.error}"
        case results: RequestSuccess[SearchResponse] => results.result.hits.hits.map(_.sourceAsMap)
          .toList.map(Materializer.cmon[Employee])
      }
    }
  }

  def updateByQuery(q: String, script: String): Future[Long] = {
    throw new UnsupportedOperationException("dao updateByQuery not supported yet")
  }

  def terminateConnection() = client.close()
}
