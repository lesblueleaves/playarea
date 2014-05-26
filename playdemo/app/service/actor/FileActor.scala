package service.actor

import helper.filemon.api.ActionContext
import helper.filemon.impl.ThumbnailCapturer
import akka.actor.Actor
import play.api.Logger
import java.io.File
import utils.Configs
import akka.actor.ActorLogging

class FileActor extends Actor with ActorLogging{
  import service.akka.FileService.{FileElem,FileInfo}
  import helper.FileHelper

  def receive = {
    case FileInfo(host,context,path,name) =>{
      Logger.debug(s"got msg:$host, $context, $path, $name")
      val actionContext = new ActionContext( path, "", "", "", "");
      FileHelper.download(FileHelper.getPathString(host,context),path, name)
      
      val absPath = FileHelper.getAbsPath(path, name)
      onModified(absPath,new ActionContext(name,"","","","") )
    }
    case _ =>
      println("anythin else!")
  }
  
  def  onModified(path:String,actionContext:ActionContext){
    def capture = new ThumbnailCapturer
    capture.onModified(new File(FileHelper.getPathString(Configs.downloadDir,path)), actionContext)
  }
}