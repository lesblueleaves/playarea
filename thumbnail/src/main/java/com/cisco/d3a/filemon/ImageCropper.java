package com.cisco.d3a.filemon;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.api.LocalStorage;
import com.cisco.d3a.filemon.impl.support.ImageResizer;
import com.cisco.d3a.filemon.util.FileHelper;

public class ImageCropper {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private CaptureSpec captureSpec;
	private List<CaptureSpec> resizeSpecs;
	private LocalStorage localStorage;
	
	public CaptureSpec getCaptureSpec() {
		return captureSpec;
	}

	public void setCaptureSpec(CaptureSpec captureSpec) {
		this.captureSpec = captureSpec;
	}

	public List<CaptureSpec> getResizeSpecs() {
		return resizeSpecs;
	}

	public void setResizeSpecs(List<CaptureSpec> resizeSpecs) {
		this.resizeSpecs = resizeSpecs;
	}
	
	public LocalStorage getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}

	public void process(File localFile, String contextPath) {
        File imageFolder = localStorage.getTempFile(contextPath, true);
        File tImageFolder = new File(imageFolder, captureSpec.getPath());
        File thumbFolder = localStorage.getThumbnailFile(contextPath, true);
        List<File> thumbnailFiles = FileHelper.sort(localFile, tImageFolder);
        resizeThumbnails(localFile, thumbFolder, thumbnailFiles, contextPath);
	}
	
    private void resizeThumbnails(File file, File to, List<File> thumbnailFiles, String contextPath) {
        final int numberOfImages = thumbnailFiles.size();
        int count = 0;
        for(int i = 0; i < numberOfImages; i ++) {
            try {
                ImageResizer imageResizer = new ImageResizer(thumbnailFiles.get(i));
                for(CaptureSpec resizeSpec : resizeSpecs) {
                    int numOfSpecImgs = resizeSpec.getCapturePages(numberOfImages);
                    if(i < numOfSpecImgs) {
                        File dest = resizeSpec.getOutputFile(to, file, i + 1);
                        CaptureSpec.Size size = resizeSpec.getCaptureSize(imageResizer.getImage());
                        if(size.isValid()) {
                            imageResizer.resize(dest, size.width, size.height, resizeSpec.getType());
                            count ++;
                        } else {
                        	LOGGER.warn("Illegal thumbnail size: " + size);
                        }
                    }
                }
            } catch(IOException e) {
                LOGGER.error("Error in resizing thumbnail for " + thumbnailFiles.get(i).getAbsolutePath(), e);
            }
        }
        if(count != 0) {
        	LOGGER.info(count + " thumbnails captured for " + contextPath);
        }
    }
}
