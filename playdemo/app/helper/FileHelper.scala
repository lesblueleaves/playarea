package helper
import java.io.File
import play.api.Logger
import play.api.libs.ws.WS
import java.io.FileOutputStream
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object FileHelper {

  val dir = "down/"

  def download(urlString: String, name: String) = {
    Logger.info("Gonna to fetch an URL: " + urlString)
    WS.url(urlString).get.map { response =>
      val bytes = response.getAHCResponse.getResponseBodyAsBytes()
      Logger.info("Gonna to fetch an URL: " + bytes.length + " bytes" + " / ")

      if (!(new File(dir)).exists()) (new File(dir)).mkdir()

      val output = new FileOutputStream(new File(dir + name));

      output.write(bytes)
      output.close
      Logger.info("download finished!")
    }
  }

}