package service.actor

import helper.filemon.api.ActionContext
import helper.filemon.impl.ThumbnailCapturer
import akka.actor.Actor
import play.api.Logger
import java.io.File

class FileActor extends Actor{
  import service.akka.FileService.{FileElem,FileInfo}
  import helper.FileHelper

  def receive = {
    case FileElem(url, name) =>
      FileHelper.download(url, name)
    case FileInfo(host,context,path,name) =>{
      println(s"got msg:$host, $context, $path, $name")
      val actionContext = new ActionContext( path, "", "", "", "");
      FileHelper.download(host+context+path+name, name)
      onModified(path+name,new ActionContext(name,"","","","") )
    }
    case _ =>
      println("anythin else!")
  }
  
  def  onModified(fullPath:String,actionContext:ActionContext){
    def capture = new ThumbnailCapturer
    Logger.info(s"fullPath:$fullPath")
    capture.onModified(new File("down/"+fullPath), actionContext)
  }
}