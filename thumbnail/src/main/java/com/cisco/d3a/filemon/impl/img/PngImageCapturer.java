package com.cisco.d3a.filemon.impl.img;

import java.io.File;

import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.impl.support.ImageResizer;

public class PngImageCapturer extends AbstractImageCapturer {
	@Override
	protected void doCapture(File file, File folder, final CaptureSpec captureSpec) throws Exception {
		ImageResizer imageResizer = new ImageResizer(file);
	    final File dest = getOutputFile(file, folder, captureSpec, 1, true);
	    CaptureSpec.Size size = captureSpec.getCaptureSize(imageResizer.getImage());
    	Thumbnails.of(file).outputFormat("png").size(size.width, size.height).toFiles(new Rename() {
			@Override
			public String apply(String name, ThumbnailParameter param) {
				return captureSpec.getPath() + "/" + name;
			}    		
			@Override
			public File apply(File file, ThumbnailParameter param) {
				return dest;
			}    		
    	});
	}
	
    public static void main(String[] args) throws Exception {
    	PngImageCapturer cap = new PngImageCapturer();
    	CaptureSpec captureSpec = new CaptureSpec();
    	captureSpec.setType("png");
    	captureSpec.setNumOfPages(2);
    	captureSpec.setRatio(100);
    	captureSpec.setPath("ORIG");
    	cap.doCapture(new File("./test/2.png"), new File("./test/"), captureSpec);
    	System.out.println("Done");
    }	
}
