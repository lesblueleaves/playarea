package com.cisco.d3a.filemon.impl.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.impl.support.ImageResizer;

/* 
 * CORE DUMP in PDFBox
 * https://issues.apache.org/jira/browse/PDFBOX-1426
 */
public class PdfBoxImageCapturer extends AbstractImageCapturer {	
	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		doCapture(PDDocument.load(file), file, folder, captureSpec);
	}
	
	@SuppressWarnings("unchecked")
	protected void doCapture(PDDocument document, File file, File folder, CaptureSpec captureSpec) throws Exception {
		int numOfPages = captureSpec.getCapturePages(document.getNumberOfPages());
		int numOfCaptured = 0;
        List<PDPage> pages = (List<PDPage>)document.getDocumentCatalog().getAllPages();
        for( int i = 0; i < numOfPages; i++ ) {
			PDPage page = pages.get( i );
			BufferedImage image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 96);
			CaptureSpec.Size size = captureSpec.getCaptureSize(image);
			File dest = getOutputFile(file, folder, captureSpec, i + 1, true);
			ImageIO.write(ImageResizer.resizeImage(image, size.width, size.height), captureSpec.getType(), dest); 
			numOfCaptured ++;
        }		
		LOGGER.debug(numOfCaptured + " image(s) captured for " + file.getName());
	}
}
