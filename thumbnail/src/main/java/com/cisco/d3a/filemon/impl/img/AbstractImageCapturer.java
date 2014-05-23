package com.cisco.d3a.filemon.impl.img;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.api.ImageCapturer;
import com.cisco.d3a.filemon.api.SecondaryImageCapturer;

public abstract class AbstractImageCapturer implements SecondaryImageCapturer {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private ImageCapturer secondary;
	
	@Override
	final public void capture(File file, File folder, CaptureSpec captureSpec) throws Exception {
		try {
			doCapture(file, folder, captureSpec);
		} catch(Exception e) {
			LOGGER.warn("Error in capturing image: " + file.getCanonicalPath());
			if(isSecondarySupported()) {
				LOGGER.warn("Cannot capture image, fallback to " + secondary.getClass().getName());
				secondary.capture(file, folder, captureSpec);
			}
		}
	}

	protected abstract void doCapture(File file, File folder, CaptureSpec captureSpec) throws Exception;
	
	@Override
	public ImageCapturer getSecondary() {
		return secondary;
	}
		
	public void setSecondary(ImageCapturer secondary) {
		this.secondary = secondary;
	}

	@Override
	public boolean isSecondarySupported() {
		return secondary != null;
	}
	
	protected File getOutputFile(File file, File folder, CaptureSpec captureSpec) {
	    return getOutputFile(file, folder, captureSpec, -1, false);
	}
	
	protected File getOutputFile(File file, File folder, CaptureSpec captureSpec, int page, boolean includeType) {
	    return new File(folder, captureSpec.getCaptureName(FilenameUtils.removeExtension(file.getName()), page, includeType));
	}
}
