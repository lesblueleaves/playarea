package com.cisco.d3a.filemon.impl.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.imageio.ImageIO;

import com.alternativagame.resource.utils.psd.PSDParser;
import com.alternativagame.resource.utils.psd.layer.PSDLayerPixelData;
import com.alternativagame.resource.utils.psd.layer.PSDLayerStructure;
import com.alternativagame.resource.utils.psd.section.PSDLayerAndMask;
import com.cisco.d3a.filemon.api.CaptureSpec;

public class PsdParserImageCapturer extends AbstractImageCapturer {
	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		PSDParser parser = new PSDParser(new FileInputStream(file)); 
		PSDLayerAndMask layerAndMask = parser.getLayerAndMask();    
		 
		List<PSDLayerStructure> layers = layerAndMask.getLayers(); 
		List<PSDLayerPixelData> images = layerAndMask.getImageLayers(); 
        final int numOfPages = captureSpec.getCapturePages(layers.size());

        int i = 0; 
        int numOfCaptured = 0;
		for (PSDLayerStructure layer : layers) { 
		    PSDLayerPixelData pixelData = images.get(i); 
		    BufferedImage image = pixelData.getImage(); 
		    if (image != null) {
			    File dest = getOutputFile(file, folder, captureSpec, i + 1, true);
		    	ImageIO.write(image, captureSpec.getType(), dest); 
		    	numOfCaptured ++;
		    }
			if(++ i >= numOfPages) break;
		}		
		LOGGER.debug(numOfCaptured + " image(s) captured for " + file.getName());
	}
}
