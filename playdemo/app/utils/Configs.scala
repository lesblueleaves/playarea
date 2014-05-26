package utils

import play.api.Play._


object Configs {
  
  val host = current.configuration.getString("host").getOrElse("http://localhost")
  val context = current.configuration.getString("context").get
  val dir = current.configuration.getString("dir").get
  val downloadDir = current.configuration.getString("down").get
  val encode = current.configuration.getString("encode").get
}