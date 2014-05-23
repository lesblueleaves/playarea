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
import controllers.routes
import models.Updatable
import service.akka.FileService

object Space extends Controller {
  
  val host="http://10.140.92.115:9000"
  val context="/api/file/data/"
  val dir ="var/SH/"

  def index(path: String) = Action { request =>

    def docs = Updatable.find[Document]("path" -> "/111/22")
    Ok(views.html.space(docs))
  }

  def get(path: String) = Action {
    Ok.sendFile(
      content = new java.io.File(dir+path),
      fileName = _ => "usaidit.pdf")
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
      Redirect(routes.Space.index(path))
    }.getOrElse {
      Redirect(routes.Space.index(path)).flashing(
        "error" -> "Missing file")
    }
  }

  def file(path: String) = Action { request =>
    FileService.onFileChange(path, "thisisit.pdf")
    Ok("ok")
  }

}