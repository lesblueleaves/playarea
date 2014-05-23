package models

import play.api.db.DB
import play.api.Play.current
import anorm.SqlParser._
import org.joda.time.DateTime
import anorm._
import models.Parsers
import play.Logger

object Updatable {

//   def find[A](sql: String)(implicit ma: Manifest[A]) = {
//    val a = ma.runtimeClass.getSimpleName
//    
//    DB.withConnection { implicit conn =>
//      SQL(sql).as(Parsers.parsers(a) *).asInstanceOf[List[A]]
//    }
//  }
  
    def save(id: Option[Long], tableName: String, values: (String, Any)*) = {
    DB.withConnection { implicit c =>
      val on = valuesToParameters(values:_*)
      id.fold {

        val i = values.map(x => x._1).mkString(",") 
        val u = values.map(x => "{" + x._1 + "}").mkString(",")
        val q = "insert into `" + tableName + "`(" + i + ") values(" + u + ")"

//        val created = "created" -> dateTimeToParameterValue(DateTime.now)
        Logger.info(s"q:$q")
        SQL(q).on(on: _*).executeInsert().get
      } { real =>
        val u = values.map(x => x._1 + "={" + x._1 + "}").mkString(",")
        val q = "update `" + tableName + "` set " + u + " where id=" + id.get
        Logger.debug(q)
        SQL(q).on(on: _*).executeUpdate
        real
      }
    }
  }
   
  def find[A](values: (String, Any)*)(implicit e: Manifest[A]): List[A] = {
    val conditions = values.map(v => v._1 + "={" + v._1 + "}").mkString(" and ")
      
    find(conditions, values: _*)
  }
  
   def find[A](conditions: String, values: (String, Any) *)(implicit e: Manifest[A]): List[A] = {
    DB.withConnection { implicit c =>
      val className = e.runtimeClass.getSimpleName

      val on = valuesToParameters(values:_*)

      val cs = if (conditions.size > 0) {
        " where " + conditions
      } else {
        " "
      }
      
      val q = "select * from `" + className + "` " + cs 
      
      Logger.info(s"q::$q")
      SQL(q).on(on: _*).as(Parsers.parsers(className) *).asInstanceOf[List[A]]
      // SQL(q).as(Parsers.parsers(className).singleOpt).asInstanceOf[Option[A]]
    }
  }
   
    def valuesToParameters(values: (String, Any)*) = {
    values.map { x =>
      (x._1, x._1 match {
        case y if y.contains("Date") => {
          x._2.asInstanceOf[Option[org.joda.time.DateTime]].map{ d =>
            dateTimeToParameterValue(d)
          } getOrElse {
            dateTimeToParameterValue(DateTime.now)
          }
        }
        case _ =>
          x._2 match {
            case y: DateTime => dateTimeToParameterValue(y)
            case z => toParameterValue(z)
          }
      })
    }
  }
    
  def dateTimeToParameterValue(a: DateTime)(implicit p: ToStatement[String]): ParameterValue[String] = ParameterValue(a.toString("yyyy-MM-dd hh:mm:ss"), p)

  def toParameterValue[A](a: A)(implicit p: ToStatement[A]): ParameterValue[A] = ParameterValue(a, p)
   
}

trait Updatable[A] {
  self: A =>

  val id: Option[Long]

  def save(values: (String, Any)*): Unit = {

    Updatable.save(id, getClass.getSimpleName, values: _*)
  }

  def save(): Unit = {

    val fs = self.getClass.getDeclaredFields
    val values = fs.filter(f => !f.getName.equalsIgnoreCase("id") && !f.getName.equalsIgnoreCase("created") && !f.getName.equalsIgnoreCase("updated")).map { f =>
      f.setAccessible(true)
      (f.getName, f.get(self))
    }
    
    save(values: _*)
  }

  
}
