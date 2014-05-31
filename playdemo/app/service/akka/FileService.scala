package service.akka

import java.net._
import java.io._
import akka.actor.ActorSystem
import akka.actor.Props
import service.actor.FileActor

object FileService {
  val system = ActorSystem("FileSystem")
  val fileActor = system.actorOf(Props[FileActor], name = "fileService")
//  val picActor = system.actorOf(Props[LocalActor], name = "LocalActor")

  case class FileElem(url: String, name: String)
  case class FileInfo(host:String,context:String,path: String, name: String)

  def onFileChange(url: String, name: String) = {
    def fileElem = FileElem(url, name)
    fileActor ! (fileElem)
    println("msg handle finished!")
  }

  def onFileChange(host: String, context:String, path: String, name: String) = {
    def fileInfo = FileInfo(host,context,path, name)
    fileActor ! (fileInfo)
    println("msg handle finished!")
  }
  
  
}