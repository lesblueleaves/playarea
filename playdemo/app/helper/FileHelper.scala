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

  def download(prefix: String, path:String, name: String) = {
    val url = prefix + getEncodedFullPath(path,name)
    val fullPath = getFullPath(path,name)
    Logger.info("Gonna to fetch an URL: " + url )
    Logger.info("fullPath: " + fullPath )
//    Logger.info("Gonna to fetch an URL:encoded " + UriEncoding.encodePathSegment(urlString, encode) )
    WS.url(url).get.map { response =>
      val bytes = response.getAHCResponse.getResponseBodyAsBytes()

      if (!(new File(dir+path)).exists()) new File(dir+path).mkdirs()
      val output = new FileOutputStream(new File(dir + fullPath));

      output.write(bytes)
      output.close
      Logger.info("download finished!")
    }
  }
  
  def getFullPath(path:String, name: String):String = if(!path.endsWith("/"))  path+"/"+name else path+name

  def getEncodedFullPath(path:String, name: String):String = UriEncoding.encodePathSegment(getFullPath(path,name),encode)
}