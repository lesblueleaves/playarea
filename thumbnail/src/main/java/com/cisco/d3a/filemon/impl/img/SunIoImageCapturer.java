package com.cisco.d3a.filemon.impl.img;

import java.io.File;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.impl.support.ImageResizer;

public class SunIoImageCapturer extends AbstractImageCapturer {
	@Override
	protected void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		ImageResizer imageResizer = new ImageResizer(file);
	    File dest = getOutputFile(file, folder, captureSpec, 1, true);
	    CaptureSpec.Size size = captureSpec.getCaptureSize(imageResizer.getImage());
		imageResizer.resize(dest, size.width, size.height, captureSpec.getType());
	}
}
