package helper.filemon.impl.support

import java.io.File
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.IOException
import java.awt.Graphics2D
import java.awt.AlphaComposite
import java.awt.RenderingHints
//import org.imgscalr.Scalr

object ImageResizerHelper {
  def resizeImage(originalImage: BufferedImage, width: Int, height: Int): BufferedImage = resizeImage(originalImage, width, height, false);

  def resizeImage(originalImage: BufferedImage, width: Int, height: Int, withHint: Boolean): BufferedImage = {
    //		def stype:Int = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : originalImage.getType();
    val stype = BufferedImage.TYPE_INT_ARGB;
    val resizedImage: BufferedImage = new BufferedImage(width, height, stype);
    val g: Graphics2D = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, width, height, null);
    g.dispose();
    g.setComposite(AlphaComposite.Src);

    if (withHint) {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    }
    resizedImage;
  }

  //	def  resizeWithScalr(originalImage:BufferedImage,  width:Int, height:Int,  withHint: Boolean):BufferedImage= {
  //		return Scalr.resize(originalImage, width, height);
  //	}
}

class ImageResizer(from:File,image:BufferedImage) {

  def this(from: File) = this(from,ImageIO.read(from))

//  def this (image: BufferedImage) = this(new File(""),image)

  @throws[IOException]
  def getImage:BufferedImage = {
    if (image == null)  ImageIO.read(from);
    if (image == null) throw new IOException("Can not load image from " + from.getName());
    image
  }

  @throws[IOException]
  def resize(to: File, width: Int, height: Int, stype: String): Boolean = resize(to, width, height, stype, false)

  @throws[IOException]
  def resize(to: File, width: Int, height: Int, stype: String, withHints: Boolean): Boolean = {
    val resizedImage: BufferedImage = ImageResizerHelper.resizeImage(getImage, width, height, withHints);
    return ImageIO.write(resizedImage, stype, to);
  }
}