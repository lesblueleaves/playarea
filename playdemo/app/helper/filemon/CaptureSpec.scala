package helper.filemon

import play.api.libs.iteratee.Enumerator
import java.awt.image.BufferedImage
import java.io.File
import org.apache.commons.io.FilenameUtils

case class CaptureSpecSize(width: Int, height: Int) {
  def isValid:Boolean = {
    if (width > 0 && height > 0) true
    false
  }
}

class CaptureSpec(name: String, stype: String = "png",
    _path: String, ratio: Int = 100,maxWidth:Int,maxHeight:Int,numOfPages:Int) {
  
  import helper.filemon.ScaleSpec._
  val path= _path
  val picType = stype
  var scaleSpec: ScaleSpec.Value = ScaleSpec.KEEP_RATIO

  def getCaptureName(baseName: String, page: Int): String = {
    getCaptureName(baseName, page, false);
  }

  def getCaptureName(baseName: String, page: Int, includeType: Boolean): String = {
    if (includeType) {
      "s$page.$stype"
    } else {
      "s$stype"
    }
  }
  def getCapturePages(pageNumber:Int):Int= {
		if(numOfPages < 1 || numOfPages > pageNumber)  pageNumber
		 numOfPages;
	}

  def getCaptureSize(image: BufferedImage): CaptureSpecSize = {
    getCaptureSize(image.getWidth(), image.getHeight());
  }

  def getCaptureSize(originalWidth: Int, originalHeight: Int): CaptureSpecSize = {
    var width: Int = originalWidth;
    var height: Int = originalHeight;

    if (scaleSpec == ScaleSpec.KEEP_RATIO) {
      if (ratio != 100) {
        width = originalWidth * ratio / 100;
        height = originalHeight * ratio / 100;
      }
      var wRatio: Double = 100
      var hRatio: Double = 100
      if (maxWidth > 0 && maxWidth < width) {
        wRatio = (maxWidth) / width;
      } else {
        wRatio = 1;
      }
      if (maxHeight > 0 && maxHeight < height) {
        hRatio = (maxHeight) / height;
      } else {
        hRatio = 1;
      }
      var r: Double = Math.min(wRatio, hRatio);
      if (maxWidth != 0) width = (r * width).toInt;
      if (maxHeight != 0) height = (r * height).toInt;
    } else {
      if (maxWidth > 0) width = maxWidth;
      if (maxHeight > 0) height = maxHeight;
    }
    return new CaptureSpecSize(width, height);
  }

  def getOutputFile(folder: File, file: File, page: Int): File = {
    var tfolder: File = new File(folder, path);
    tfolder.mkdirs();
    new File(tfolder, getCaptureName(FilenameUtils.removeExtension(file.getName()), page, true));
  }

  override def toString(): String = {
    var buf = new StringBuilder()
    buf.append("(").append(path).append('.').append(stype).append(") - (");
    buf.append(maxWidth).append(" x ").append(maxHeight).append(") * ").append(numOfPages);
    return buf.toString();
  }

}

object ScaleSpec extends Enumeration {
  type ScaleSpec = Value
  val SCALE_TO_FIT, KEEP_RATIO = Value
}