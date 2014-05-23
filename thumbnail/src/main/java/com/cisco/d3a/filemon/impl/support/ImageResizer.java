package com.cisco.d3a.filemon.impl.support;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class ImageResizer {
    private File from;
	private BufferedImage image;
	
	public ImageResizer(File from)  {
		this.from = from;
	}
	
	public ImageResizer(BufferedImage image)  {
        if(image == null) throw new IllegalArgumentException("Can not be null");
		this.image = image;
	}
	
	public BufferedImage getImage() throws IOException {
        if(image == null) image = ImageIO.read(from);
        if(image == null) throw new IOException("Can not load image from " + from.getName());
		return image;
	}

	public boolean resize(File to, int width, int height, String type) throws IOException {
		return resize(to, width, height, type, false); 		
	}
	
	public boolean resize(File to, int width, int height, String type, boolean withHints) throws IOException {
		BufferedImage resizedImage = resizeImage(getImage(), width, height, withHints);
		return ImageIO.write(resizedImage, type, to); 		
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
		return resizeImage(originalImage, width, height, false);
    }
 
	public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height, boolean withHint) {
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : originalImage.getType();
		type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
	 
		if(withHint) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}	 
		return resizedImage;
    }
	
	public static BufferedImage resizeWithScalr(BufferedImage originalImage, int width, int height, boolean withHints) {
		return Scalr.resize(originalImage, width, height);
	}
}
