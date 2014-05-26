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
    val absPath = getAbsPath(path,name)
    val url = getPathString(prefix, getEncodedPath(absPath))
    
    Logger.info(s"Gonna to fetch an URL:$url")
    Logger.info(s"absPath:$absPath" )
    WS.url(url).get.map { response =>
      val bytes = response.getAHCResponse.getResponseBodyAsBytes()

      if (!(new File(dir+path)).exists()) new File(dir+path).mkdirs()
      val output = new FileOutputStream(new File(dir + absPath));

      output.write(bytes)
      output.close
      Logger.info("download finished!")
    }
  }
  
  def getAbsPath(path:String, name: String):String = if(!path.endsWith("/"))  path+"/"+name else path+name

  def getEncodedPath(path:String):String = UriEncoding.encodePathSegment(path,encode)

  def getPathString(path:String*):String=path.mkString("/")
  
  def isExists(path:String):Boolean=new File(path).exists() && new File(path).isFile()
}