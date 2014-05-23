package helper.filemon.api

import java.io.File
import helper.filemon.CaptureSpec
import play.api.Logger

trait ImageCapturer {
  @throws[Exception]
  def capture(file: File, folder: File, captureSpec: CaptureSpec){
    try{
      doCapture(file,folder,captureSpec)
    }catch{
      case x:Exception =>{
        Logger.warn("Error in capturing image: " + file.getCanonicalPath());
      }
    }
  }
  
  def doCapture(file: File, folder: File, captureSpec: CaptureSpec)
}