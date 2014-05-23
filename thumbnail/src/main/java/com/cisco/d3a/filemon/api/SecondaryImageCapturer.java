package com.cisco.d3a.filemon.api;


public interface SecondaryImageCapturer extends ImageCapturer {
	ImageCapturer getSecondary();
	boolean isSecondarySupported();
}
