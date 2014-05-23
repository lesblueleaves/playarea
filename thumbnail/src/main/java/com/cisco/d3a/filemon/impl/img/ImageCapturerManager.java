package com.cisco.d3a.filemon.impl.img;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.api.ImageCapturer;

public class ImageCapturerManager implements InitializingBean {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Map<String, ImageCapturer> imageCapturers;
    private String fallbackType;

    public void setFallbackType(String fallbackType) {
        this.fallbackType = fallbackType;
    }

    public Map<String, ImageCapturer> getImageCapturers() {
		return imageCapturers;
	}

	public void setImageCapturers(Map<String, ImageCapturer> imageCapturers) {
		this.imageCapturers = imageCapturers;
	}

	public ImageCapturer getImageCapturer(String type) {
        ImageCapturer capturer = imageCapturers.get(type.toLowerCase());
        if(capturer == null && fallbackType != null) {
            capturer = imageCapturers.get(fallbackType);
            if(capturer != null) {
                LOGGER.debug("Unknown type: " + type + ", fallback to " + fallbackType);
            }
        }
        return capturer;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Supported file types: " + imageCapturers.keySet());
	}
}
