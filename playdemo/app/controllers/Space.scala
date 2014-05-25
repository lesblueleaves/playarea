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

object Space extends Controller {
  
  val host="http://localhost:9000"
  val context="/api/file/data/"
  val dir ="var/SH/"

  def index(path: String) = Action { request =>
  
    println(s"path:$path")
    def docs = Updatable.find[Document]("path" -> path)
    Ok(views.html.space(docs,path))
  }

  def get(path: String) = Action {
    Ok.sendFile(
      content = new java.io.File(dir+path),
      fileName = _ => "")
  }

  def upload(path: String) = Action(parse.multipartFormData) { request =>
    Logger.info(s"path=>$path")
    request.body.file("doc").map { doc =>
      import java.io.File
      val filename = doc.filename
      val contentType = doc.contentType
      doc.ref.moveTo(new File(s"$dir$filename"))

      val document = Document(None, Option(" "), Option(" "), Option(filename), Option(" "), Option(121), Option(1234l), DateTime.now(), Option(path), Option(false));
      //      document.save(None,"Document","uid"->"les","owner"->"les","name"->"","description"->,
      //    		  	"size"->111,"lastModified"->123l,"dateCreated"->DateTime.now(),"path"->path, "folder"->false)
      document.save
      FileService.onFileChange(host, context,"",filename)
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