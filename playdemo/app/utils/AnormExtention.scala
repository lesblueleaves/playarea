package utils

import org.joda.time._
import org.joda.time.format._
import anorm._

object AnormExtention {
val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSS")

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull {
    (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
        case d: java.sql.Date => Right(new DateTime(d.getTime))
        case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass))
      }
  }

  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime) {
      s.setTimestamp(index, new java.sql.Timestamp(aValue.withMillisOfSecond(0).getMillis))
    }
  }

  class RichSQL(val query: String, val parameterValues: (Any, ParameterValue[Any])*) {
    /**
     * Convert this object into an anorm.SqlQuery
     */
    def toSQL = SQL(query).on(parameterValues: _*)

    /**
     * Similar to anorm.SimpleSql.on, but takes lists instead of single values.
     * Each list is converted into a set of values, and then passed to anorm's
     * on function when toSQL is called.
     */
    def onList[A](args: (String, Iterable[A])*)(implicit toParameterValue: (A) => ParameterValue[A]) = {
      val condensed = args.map {
        case (name, values) =>
          val search = "{" + name + "}"
          val valueNames = values.zipWithIndex.map { case (value, index) => name + "_" + index }
          val placeholders = valueNames.map { name => "{" + name + "}" }
          val replace = placeholders.mkString(",")
          val converted = values.map { value => toParameterValue(value).asInstanceOf[ParameterValue[Any]] }
          val parameters = valueNames.zip(converted)
          (search, replace, parameters)
      }
      val newQuery = condensed.foldLeft(query) {
        case (nq, (search, replace, _)) =>
          nq.replace(search, replace)
      }
      val newValues = parameterValues ++ condensed.map { case (_, _, parameters) => parameters }.flatten
      new RichSQL(newQuery, newValues: _*)
    }
  }

  object RichSQL {
    def apply[A](query: String) = new RichSQL(query)
  }
}