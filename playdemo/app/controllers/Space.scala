package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import models.Document
import play.api.Logger
import java.io.File
import org.joda.time.DateTime
import models.Updatable
import service.akka.FileService
import play.utils.UriEncoding
import utils.Configs
import helper.FileHelper

object Space extends Controller {

  def index(path: String) = Action { request =>
    Logger.debug(s"path=>$path")
    def docs = Updatable.find[Document]("path" -> path)
    Ok(views.html.space(docs,path))
  }

  def get(path: String) = Action {
    val dpath = UriEncoding.decodePath(path, Configs.encode)
    Logger.info(FileHelper.getPathString(Configs.dir,dpath))
    Ok.sendFile(
        content = new java.io.File(FileHelper.getPathString(Configs.dir,dpath)),
      fileName = _ => "")
  }

  def upload(path: String) = Action(parse.multipartFormData) { request =>
    request.body.file("doc").map { doc =>
      import java.io.File
      val filename = doc.filename
      val contentType = doc.contentType
      val absPath = FileHelper.getPathString(Configs.dir,path,filename)
      val exists = FileHelper.isExists(absPath)
      Logger.debug(s"absPath=>$absPath,exists:$exists")
      
      if(exists){
//         Redirect("/400")
        Results.Conflict
      }
      
       doc.ref.moveTo(new File(absPath),true)

      val document = Document(None, Option(" "), Option(" "), Option(filename), Option(" "), Option(11), Option(1234l), DateTime.now(), Option(path), Option(false));
      document.save
      
      FileService.onFileChange(Configs.host, Configs.context,path,filename)
      Redirect(s"/api/file/list/$path")
    }.getOrElse {
      Redirect(path).flashing(
        "error" -> "Missing file")
    }
  }


}