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

object Space extends Controller {
  
//  val host="http://localhost:9000"
//  val context="/api/file/data/"
//  val dir ="var/SH/"
//  val encode ="utf-8"

  def index(path: String) = Action { request =>
    Logger.info(s"path=>$path")
    def docs = Updatable.find[Document]("path" -> path)
    Ok(views.html.space(docs,path))
  }

  def get(path: String) = Action {
    Logger.info(s"path=>$path")
    val dpath = UriEncoding.decodePath(path, Configs.encode)
    Logger.info(Array(Configs.dir,dpath).mkString("/"))
    Ok.sendFile(
//      content = new java.io.File(dir+dpath),
        content = new java.io.File(Array(Configs.dir,dpath).mkString("/")),
      fileName = _ => "")
  }

  def upload(path: String) = Action(parse.multipartFormData) { request =>
    Logger.info(s"path=>$path")
   
    request.body.file("doc").map { doc =>
      import java.io.File
      val filename = doc.filename
      val contentType = doc.contentType
      val absPath= Array(Configs.dir,path,filename).mkString("/")
      
      
       Logger.info(s"path=>$absPath")
      
      val exists = FileService.isExists(absPath)
      Logger.debug(s"exists:$exists")
      
      if(exists){
//         Redirect("/400")
        Results.Conflict
      }
      
//      doc.ref.moveTo(new File(s"$dir$path/$filename"))
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

  def file(path: String) = Action { request =>
    FileService.onFileChange(path, "thisisit.pdf")
    Ok("ok")
  }

}