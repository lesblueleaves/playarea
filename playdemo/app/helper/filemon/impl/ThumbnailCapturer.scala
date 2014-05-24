package helper.filemon.impl

import helper.filemon.CaptureSpec
import java.io.File
import helper.filemon.api.ActionContext
import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import helper.filemon.api.ImageCapturer
import helper.filemon.MSOfficeImageCapturer
import helper.filemon.api.LocalStorage
import play.api.Logger
import java.io.IOException
import org.apache.commons.io.FileUtils
import helper.filemon.CaptureSpecSize
import helper.filemon.impl.support.ImageResizer

object ThumbnailCapturer {
  val resizeSpecs = List[CaptureSpec](new CaptureSpec("large", "png", "L", 100, 0, 0, -1),
    new CaptureSpec("medium", "png", "M", 100, 480, 480, -1), new CaptureSpec("small", "png", "S", 100, 180, 180, -1))
  val urlPrefix: String = ""
  val endPoint: String = ""
}

class ThumbnailCapturer{

  def onModified(file: File, context: ActionContext) = {
    try {
      val fullPath: String = context.path
      val ext: String = FilenameUtils.getExtension(fullPath)
      val before = DateTime.now().getMillis()
      val localStorage: LocalStorage = new LocalStorageImpl
      val captureSpec = new CaptureSpec("origin", "png", "ORIG", 100, 0, 0, -1)

      val imageCapture: ImageCapturer = new MSOfficeImageCapturer
      val imageFolder: File = localStorage.getTempFile(context.path, true);

      println(s"imageFolder:$imageFolder")
      val tImageFolder = new File(imageFolder, captureSpec.path);
      tImageFolder.mkdirs();

      val thumbFolder: File = localStorage.getThumbnailFile(context.path, true);
      var thumbnailGenerated = false;

      try {
        Logger.info("Capture thumbnails for " + context.path);
        imageCapture.capture(file, tImageFolder, captureSpec)
        thumbnailGenerated = true
      } catch {
        case x: IOException => {
          Logger.error("Error in capturing thumbnail for " + fullPath);
        }
        case x: Exception => {
          Logger.error("Error in capturing thumbnail for " + fullPath);
        }
      }

      if (thumbnailGenerated) {
        val thumbnailFiles: List[File] = tImageFolder.listFiles().toList

        if (thumbnailFiles.isEmpty) {
          resizeThumbnails(file, thumbFolder, thumbnailFiles, context);
          Logger.info("Thumbnail " + fullPath + " captured in " + (System.currentTimeMillis() - before) + " ms.");
        } else {
          Logger.info("No thumbnails captured for " + fullPath);
        }
      }

    } catch {
      case x: Exception => throw x
    }
  }

  def resizeThumbnails(file: File, to: File, thumbnailFiles: List[File], context: ActionContext) {
    val numberOfImages = thumbnailFiles.size
    // Delete old thumbnail images
    for (resizeSpec <- ThumbnailCapturer.resizeSpecs) {
      val dest = new File(to, resizeSpec.path);
      try {
        FileUtils.deleteDirectory(dest);
      } catch {
        case x: IOException => throw x
      }
    }

    for (i <- 0 until numberOfImages) {
      try {
        val imageResizer = new ImageResizer(new File(""));
        ThumbnailCapturer.resizeSpecs.foreach { resizeSpec =>
          val numOfSpecImgs = resizeSpec.getCapturePages(numberOfImages);
          if (i < numOfSpecImgs) {
            val dest: File = resizeSpec.getOutputFile(to, file, i + 1);
            val size: CaptureSpecSize = resizeSpec.getCaptureSize(imageResizer.getImage);
            if (size.isValid) {
              imageResizer.resize(dest, size.width, size.height, resizeSpec.picType);
            } else {
              Logger.warn("Illegal thumbnail size: " + size);
            }

          }
        }
      } catch {
        case x: IOException =>
          throw x
          Logger.error("Error in resizing thumbnail for " + thumbnailFiles(i).getAbsolutePath());
      }

    }
  }
}