package helper.filemon

import java.io.File
import play.api.Logger
import scala.collection.immutable.HashMap
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteWatchdog
import org.apache.commons.exec.Watchdog
import org.apache.commons.exec.TimeoutObserver
import scala.sys.process._
import helper.filemon.api.ImageCapturer
import helper.filemon.impl.ThumbnailCapturer
import helper.filemon.api.ActionContext


class MSOfficeImageCapturer extends ImageCapturer {
  val pathToOfficeToPng: String = "widget/OfficeToPng.exe"

  def doCapture(from: File, to: File, captureSpec: CaptureSpec) = {
    try {
      if (from.getName().endsWith("xlsx")) {
        if (from.length() <= 8746) ""; // empty excel file
      } else if (from.getName().endsWith("pptx")) {
        if (from.length() <= 27140) ""; // empty powerpoint file
      }

      val cmd = Seq(pathToOfficeToPng, "-i", from.getCanonicalPath(), "-o", to.getCanonicalPath())
      Logger.info("Executing command: " + cmd.toList);
      println(cmd.toList);
      //        cmd.lines 
      Process(cmd)!

    } catch {
      case x: Exception => throw x
    }
  }
}

object Main extends App {
  println("Hello World: " + (args mkString ", "))
//  val cap: MSOfficeImageCapturer = new MSOfficeImageCapturer()
//  val captureSpec = new CaptureSpec("origin", "png", "ORIG", 100, 0, 0, -1)
//  cap.doCapture(new File("down/ASS.xps"), new File("temp"), captureSpec)

  //	Process("cmd", Seq("cd f:/"))!
 def cap:ThumbnailCapturer = new ThumbnailCapturer
 cap.onModified(new File("down/ASS.xps"), new ActionContext("ASS.xps","","","",""))

}