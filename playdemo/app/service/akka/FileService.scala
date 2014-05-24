package service.akka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import java.net.URL
import java.net._
import java.io._
import _root_.java.io.Reader
import org.xml.sax.InputSource
import helper.filemon.api.ActionContext
import org.joda.time.DateTime

object FileService {
  val system = ActorSystem("FileSystem")
  val fileActor = system.actorOf(Props[FileService], name = "fileService")

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


class FileService extends Actor {
  import service.akka.FileService.{FileElem,FileInfo}
  import helper.FileHelper

  def receive = {
    case FileElem(url, name) =>
      println(s"got msg:$url, $name")
      FileHelper.download(url, name)
    case FileInfo(host,context,path,name) =>{
      println(s"got msg:$host, $context, $path, $name")
      val actionContext = new ActionContext( path, "", "", "", "");
      FileHelper.download(host+context+path+name, name)
    }
    case _ =>
      println("anythin else!")
  }
  
  def  onModified(actionContext:ActionContext){
    
  }

}