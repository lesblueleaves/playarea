package com.cisco.d3a.filemon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kevinsawicki.timeago.TimeAgo;

public class StatisticsHandler {
	private Date bootTime = new Date();
	
	private int errorCount;
	private int fileProcessed;
	private int fileDownloaded;
	private int fileIndexed;
	private int fileCaptured;
	private int fileQueued;
	private int thumbnailCaptured;
	private int etagCacheSize;
	
	public int getFileProcessed() {
		return fileProcessed;
	}
	
	public int getFileDownloaded() {
		return fileDownloaded;
	}

	public int getFileIndexed() {
		return fileIndexed;
	}

	public int getThumbnailCaptured() {
		return thumbnailCaptured;
	}
	
	public int getFileCaptured() {
		return fileCaptured;
	}
	
	public int getEtagCacheSize() {
		return etagCacheSize;
	}

	public int getFileQueued() {
		return fileQueued;
	}
	
	public void incFileProcessed() {
		this.fileProcessed ++;
	}

	public void incFileIndexed() {
		this.fileIndexed ++;
	}

	public void incFileDownloaded() {
		this.fileDownloaded ++;
	}

	public void incErrorCount() {
		this.errorCount ++;
	}

	public void incThumbnailCaptured(int count) {
		this.thumbnailCaptured += count;
	}

	public void incFileCaptured(int count) {
		this.fileCaptured += count;
	}

	public void incFileQueued(int count) {
		this.fileQueued += count;
	}

	public void decFileQueued(int count) {
		this.fileQueued -= count;
	}

	public void incEtagCacheSize(int count) {
		this.etagCacheSize += count;
	}

	public void setEtagCacheSize(int etagCacheSize) {
		this.etagCacheSize = etagCacheSize;
	}

	public Date getBootTime() {
		return bootTime;
	}	
	
	public String toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("etag", etagCacheSize);
			json.put("error", errorCount);
			json.put("boot", DF.format(bootTime));
			json.put("up", ago.timeAgo(bootTime));
			
			JSONObject fjson = new JSONObject();
			fjson.put("D", fileDownloaded);
			fjson.put("P", fileProcessed);
			fjson.put("C", fileCaptured);
			fjson.put("T", thumbnailCaptured);
			fjson.put("I", fileIndexed);
			fjson.put("Q", fileQueued);
			json.put("files", fjson);			
		} catch (JSONException e) {
		}
		return json.toString();
	}
	
	private static final TimeAgo ago = new TimeAgo();
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
