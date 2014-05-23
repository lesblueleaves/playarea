package com.cisco.d3a.filemon.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.d3a.filemon.api.ActionContext;
import com.cisco.d3a.filemon.api.FileProcessor;
import com.cisco.d3a.filemon.api.LocalStorage;
import com.cisco.d3a.filemon.util.FileHelper;
import com.cisco.d3a.filemon.util.FileManager;

public class FileCleaner implements FileProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileCleaner.class);

	private LocalStorage localStorage;
	private FileManager fileManager;

	public void setLocalStorage(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}
	
	@Override
	public void onModified(File file, ActionContext context) throws Exception {
		context.setProperty("etag", null);
		fileManager.setEtag(context.getPath(), null);
		
		File destFile = localStorage.getLocalFile(context.getPath(), false);
		if(destFile != null && destFile.exists()) {
			FileHelper.deleteFile(destFile);			
			LOGGER.info(destFile.getAbsolutePath() + " is deleted");
		}
		File tFile = localStorage.getThumbnailFile(context.getPath(), false);
		if(tFile != null && tFile.exists()) {
			FileHelper.deleteFile(tFile);			
			LOGGER.info(tFile.getAbsolutePath() + " is deleted");
		}
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}	
}
