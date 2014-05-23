package com.cisco.d3a.filemon.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

public class CaptureSpec implements Serializable {
	private static final long serialVersionUID = -7101449013940184162L;

	public static class Size implements Serializable {
		private static final long serialVersionUID = -1117371317161038143L;

		final public int width;
		final public int height;
		
		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public boolean isValid() {
			return width > 0 && height > 0;
		}
		
		public String toString() {
			return "{" + width + ","+ height + "}";
		}
	}
	
	private String name;
	private String type = "png";
	private String path = "";
	private int ratio = 100;
	
	private int maxWidth;
	private int maxHeight;
	private int numOfPages;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("(").append(path).append('.').append(type).append(") - (");
		buf.append(maxWidth).append(" x ").append(maxHeight).append(") * ").append(numOfPages);
		return buf.toString();
	}
	
	private ScaleSpec scaleSpec = ScaleSpec.KEEP_RATIO;
	
	public String getCaptureName(String baseName, int page) {
		return getCaptureName(baseName, page, false);
	}
	
	public String getCaptureName(String baseName, int page, boolean includeType) {
		if(includeType) {
			return String.format("%d.%s", page, type);
		} else {
			return String.format("%d", page);
		}
	}
	
	public Size getCaptureSize(BufferedImage image) {
		return getCaptureSize(image.getWidth(), image.getHeight());
	}
	
	public Size getCaptureSize(int originalWidth, int originalHeight) {
		int width = originalWidth;
		int height = originalHeight;
		
		if(scaleSpec == ScaleSpec.KEEP_RATIO) {
			if(ratio != 100) {
				width = originalWidth * ratio / 100;
				height = originalHeight * ratio / 100;
			}
			double wRatio = 100, hRatio = 100;
			if(maxWidth > 0 && maxWidth < width) {
				wRatio = ((double)maxWidth) / width;
			} else {
				wRatio = 1;
			}
			if(maxHeight > 0 && maxHeight < height) {
				hRatio = ((double)maxHeight) / height;
			} else {
				hRatio = 1;
			}
			double r = Math.min(wRatio, hRatio);
			if(maxWidth != 0) width = (int)(r * width);
			if(maxHeight != 0) height = (int)(r * height);
		} else {
			if(maxWidth > 0) width = maxWidth;
			if(maxHeight > 0) height = maxHeight;
		}
		return new Size(width, height);
	}
	
	public int getCapturePages(int pageNumber) {
		if(numOfPages < 1 || numOfPages > pageNumber) return pageNumber;
		return numOfPages;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public ScaleSpec getScaleSpec() {
		return scaleSpec;
	}

	public void setScaleSpec(ScaleSpec scaleSpec) {
		this.scaleSpec = scaleSpec;
	}

	public int getNumOfPages() {
		return numOfPages;
	}

	public void setNumOfPages(int numOfPages) {
		this.numOfPages = numOfPages;
	}


	public static enum ScaleSpec {
		SCALE_TO_FIT,
		KEEP_RATIO
	}
	
    public File getOutputFile(File folder, File file, int page) {
        File tfolder = new File(folder, getPath());
        tfolder.mkdirs();
        return new File(tfolder, getCaptureName(FilenameUtils.removeExtension(file.getName()), page, true));
    }
}
