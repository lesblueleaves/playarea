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
import helper.filemon.impl.ThumbnailCapturer
import play.api.Logger
import service.actor.FileActor
import org.h2.store.fs.FileUtils
import utils.Configs

object FileService {
  val system = ActorSystem("FileSystem")
  val fileActor = system.actorOf(Props[FileActor], name = "fileService")

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
  
  def isExists(path:String):Boolean=FileUtils.exists(path)
  
}
//  
//  def  onModified(fullPath:String,actionContext:ActionContext){
//    def capture = new ThumbnailCapturer
//    Logger.info(s"fullPath:$fullPath")
//    capture.onModified(new File("down/"+fullPath), actionContext)
//  }
//
//}