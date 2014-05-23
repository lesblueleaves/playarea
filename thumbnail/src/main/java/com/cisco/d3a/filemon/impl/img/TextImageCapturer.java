package com.cisco.d3a.filemon.impl.img;

import java.io.File;
import java.io.FileReader;

import org.apache.pdfbox.TextToPDF;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.cisco.d3a.filemon.api.CaptureSpec;

public class TextImageCapturer extends AbstractImageCapturer {
	private PdfBoxImageCapturer pdfBoxImageCapturer;
	private TextToPDF app = new TextToPDF();
	
	public void setPdfBoxImageCapturer(PdfBoxImageCapturer pdfBoxImageCapturer) {
		this.pdfBoxImageCapturer = pdfBoxImageCapturer;
	}

	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		PDDocument doc = app.createPDFFromText(new FileReader(file));
		pdfBoxImageCapturer.doCapture(doc, file, folder, captureSpec);
	}
}
