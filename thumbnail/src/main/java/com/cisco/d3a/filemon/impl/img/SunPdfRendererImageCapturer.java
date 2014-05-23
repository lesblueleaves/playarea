package com.cisco.d3a.filemon.impl.img;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.api.ImageCapturer;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

public class SunPdfRendererImageCapturer extends AbstractImageCapturer implements ImageCapturer {
	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
    	RandomAccessFile raf = new RandomAccessFile(file, "r");  
        FileChannel channel = raf.getChannel();  
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()); 
        PDFFile pdffile = new PDFFile(buf);
        raf.close();
        
        int numOfCaptured = 0;
    	int pages = captureSpec.getCapturePages(pdffile.getNumPages());
    	for(int i=0;i<pages;i++) {
    		PDFPage page = pdffile.getPage(i);

    		CaptureSpec.Size size = captureSpec.getCaptureSize((int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());
			BufferedImage img = new BufferedImage(size.width, size.height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			PDFRenderer renderer = new PDFRenderer(page, g2, new Rectangle(0, 0, size.width, size.height), null, Color.WHITE);
			try {
				page.waitForFinish();
			} catch (Exception e) {
				LOGGER.error("Error in capturing image for " + file.getName() + ": " + e.getMessage());
			}
			renderer.run();
			g2.dispose();

		    File dest = getOutputFile(file, folder, captureSpec, i + 1, true);
			ImageIO.write(img, captureSpec.getType(), dest);
			numOfCaptured ++;
    	}
		LOGGER.debug(numOfCaptured + " image(s) captured for " + file.getName());
	}
}
