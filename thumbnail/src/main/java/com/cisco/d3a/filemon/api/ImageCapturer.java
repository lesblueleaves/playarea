package com.cisco.d3a.filemon.api;

import java.io.File;

public interface ImageCapturer {
	void capture(File file, File folder, CaptureSpec captureSpec) throws Exception;
}
