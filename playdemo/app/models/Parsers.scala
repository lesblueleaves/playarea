package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json._
import utils.AnormExtention._

object Parsers {

  val DocumentParser:RowParser[Document] ={
	  get[Option[Long]]("id") ~
      get[Option[String]]("uid") ~
      get[Option[String]]("owner") ~
      get[Option[String]]("name") ~
      get[Option[String]]("description") ~
      get[Option[Long]]("size") ~
      get[Option[Long]]("lastModified") ~
      get[org.joda.time.DateTime]("dateCreated") ~
      get[Option[String]]("path") ~
      get[Option[Boolean]]("folder") map {
        case id ~ uid ~ owner ~ name ~ description ~ size ~ lastModified  ~dateCreated~
          path ~ folder =>
          Document(id, uid, owner, name, description, size, lastModified,dateCreated,
            path, folder)
      }
}
  
  val parsers = Map[String,RowParser[_]](
      "Document" -> DocumentParser
      )
  
//  implicit val documentFormat = Json.format[Document]
}