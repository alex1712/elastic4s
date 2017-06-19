package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.http.index.admin.{DeleteIndexResponse, IndexExistsResponse}
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.index.mappings.PutMappingResponse
import play.api.libs.json._

import scala.annotation.implicitNotFound
import scala.util.control.NonFatal

package object playjson {

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]) = new Indexable[T] {
    override def json(t: T): String = Json.stringify(Json.toJson(t)(w))
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonHitReader[T](implicit r: Reads[T]) = new HitReader[T] {
    override def read(hit: Hit): Either[Throwable, T] = try {
      Right(Json.parse(hit.sourceAsString).as[T])
    } catch {
      case NonFatal(e) => Left(e)
    }
  }

  implicit val indexExistResponseResponseFormat = Json.reads[IndexExistsResponse]
  implicit val createIndexResponseFormat = Json.reads[CreateIndexResponse]
  implicit val deleteIndexResponseFormat = Json.reads[DeleteIndexResponse]
  implicit val putMappingResponseFormat = Json.reads[PutMappingResponse]
}
