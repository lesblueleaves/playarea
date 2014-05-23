package models

import org.joda.time.DateTime
import models.Updatable

case class Document(
  id: Option[Long],
  uid: Option[String],
  owner: Option[String],
  name: Option[String],
  description: Option[String],
  size: Option[Long],
  lastModified: Option[Long],
  dateCreated: DateTime = DateTime.now(),
  path: Option[String],
  folder: Option[Boolean]) extends Updatable[Document]
