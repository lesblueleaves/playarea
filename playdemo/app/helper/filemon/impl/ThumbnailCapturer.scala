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
import org.apache.http.client.HttpClient
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import scala.collection.mutable.HashMap

object ThumbnailCapturer {
  val resizeSpecs = List[CaptureSpec](new CaptureSpec("large", "png", "L", 100, 0, 0, -1),
    new CaptureSpec("medium", "png", "M", 100, 480, 480, -1), new CaptureSpec("small", "png", "S", 100, 180, 180, -1))
  val urlPrefix: String = "api/file/data"
  val endPoint: String = ""
}

class ThumbnailCapturer {

  def onModified(file: File, context: ActionContext) = {
    try {
      val fullPath: String = context.path
      val ext: String = FilenameUtils.getExtension(fullPath)
      val before = DateTime.now().getMillis()
      val localStorage: LocalStorage = new LocalStorageImpl
      val captureSpec = new CaptureSpec("origin", "png", "ORIG", 100, 0, 0, -1)

      val imageCapture: ImageCapturer = new MSOfficeImageCapturer
      val imageFolder: File = localStorage.getTempFile(context.path, true);

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
          println (x)
        }
        case x: Exception => {
          Logger.error("Error in capturing thumbnail for " + fullPath);
            println (x)
        }
      }
      
      if (thumbnailGenerated) {
        println("ext:"+ captureSpec.getType)
        
        val thumbnails: List[File] =tImageFolder.listFiles().filter(file =>
        		file.getName().endsWith(captureSpec.getType)).sortWith((f1,f2)=>
           FilenameUtils.getBaseName(f1.getName()).toInt <
           FilenameUtils.getBaseName(f2.getName()).toInt
            ).toList

        if (!thumbnails.isEmpty) {
          resizeThumbnails(file, thumbFolder, thumbnails, context);
          Logger.info("Thumbnail " + fullPath + " captured in " + (System.currentTimeMillis() - before) + " ms.");
        } else {
          Logger.info("No thumbnails captured for " + fullPath);
        }
        
         try {
        	 uploadThumbnails(file, thumbFolder, context);
			} catch {
			  case x:Exception => Logger.error(x.getMessage())
			  
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
        val imageResizer = new ImageResizer(thumbnailFiles(i))
        val timage = imageResizer.getImage
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
     
  	@throws[Exception]
      private def uploadThumbnails( file:File,  thumbnailFolder:File,  context:ActionContext)= {
		if(thumbnailFolder.exists() && thumbnailFolder.isDirectory()) {
			Logger.info("Uploading thumbnails under " + thumbnailFolder.getCanonicalPath());
			
//			val client:HttpClient = HttpClientFactory.createHttpClient();
			try {
				var c = 0;
//	            var thumbnails = new JSONObject();
				var thumbnails = JsObject(Seq())
	            ThumbnailCapturer.resizeSpecs.foreach {resizeSpec =>
	            	val folder= new File(thumbnailFolder, resizeSpec.path);
	                
	                val thumbArr = collectThumbnailsForSpec(context, folder, resizeSpec);
//	                thumbnails(resizeSpec.path) = thumbArr.mkString("|")
	                thumbnails = thumbnails.+((resizeSpec.path, JsString(thumbArr.mkString("|"))))
	                c += thumbArr.size
	                println(thumbArr.mkString("|"))
	                println(c)
	            }
	            println(Json.toJson(thumbnails))

			} finally {
//				client.getConnectionManager().shutdown();		
			}
        } else {
			Logger.warn("Invalid thumbnail folder: " + thumbnailFolder.getName() + " for " + context.path);
		}
	}
	
  	@throws[Exception]
	private def collectThumbnailsForSpec(context:ActionContext, folder:File, resizeSpec:CaptureSpec):ArrayBuffer[String]= {
      
  	      val thumbnails: List[File] =folder.listFiles().filter(file =>
        		file.getName().endsWith(resizeSpec.getType)).sortWith((f1,f2)=>
           FilenameUtils.getBaseName(f1.getName()).toInt <
           FilenameUtils.getBaseName(f2.getName()).toInt
            ).toList
        
        val thumbArr = new ArrayBuffer[String]()
        
		thumbnails.foreach { t=>
			 thumbArr += Array(ThumbnailCapturer.urlPrefix,context.path,resizeSpec.path,t.getName).mkString("/")
        }
		thumbArr
		
	}
  
  
  
  
}