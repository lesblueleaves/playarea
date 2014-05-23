package com.cisco.d3a.filemon.impl.img;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import psd.model.Psd;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.impl.support.ImageResizer;

public class PsdLibraryImageCapturer extends AbstractImageCapturer {
	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		Psd psd = new Psd(file); 		
	    File dest = getOutputFile(file, folder, captureSpec, 1, true);
	    CaptureSpec.Size size = captureSpec.getCaptureSize(psd.getImage().getWidth(), psd.getImage().getHeight());
	    BufferedImage image = ImageResizer.resizeImage(psd.getImage(), size.width, size.height);
    	ImageIO.write(image, captureSpec.getType(), dest); 
		LOGGER.debug(file.getName() + " captured to " + dest.getName());
	}
}
