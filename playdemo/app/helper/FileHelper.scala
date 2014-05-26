package helper
import java.io.File
import play.api.Logger
import play.api.libs.ws.WS
import java.io.FileOutputStream
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.utils.UriEncoding

object FileHelper {

  val dir = "down/"
  val encode="utf-8"

  def download(urlString: String, pathString:String, name: String) = {
    val url = urlString + UriEncoding.encodePathSegment(pathString, encode)
    Logger.info("Gonna to fetch an URL: " + url )
//    Logger.info("Gonna to fetch an URL:encoded " + UriEncoding.encodePathSegment(urlString, encode) )
    WS.url(url).get.map { response =>
      val bytes = response.getAHCResponse.getResponseBodyAsBytes()
      Logger.info("Gonna to fetch an URL: " + bytes.length + " bytes" + " / ")

      if (!(new File(dir+pathString).getParentFile()).exists()) (new File(dir+pathString).getParentFile()).mkdir()

      val output = new FileOutputStream(new File(dir + pathString));

      output.write(bytes)
      output.close
      Logger.info("download finished!")
    }
  }

}