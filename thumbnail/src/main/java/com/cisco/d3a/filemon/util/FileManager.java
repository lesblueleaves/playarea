package com.cisco.d3a.filemon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.StatisticsHandler;
import com.cisco.d3a.filemon.api.ActionContext;

public class FileManager implements InitializingBean, DisposableBean {
	private static final String CACHE_FILE_NAME = "etag.cache";
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private Properties etagCache = new Properties();
	private StatisticsHandler statisticsHandler;
		
	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
	
	private static class IntHolder {
		public IntHolder(int value) {
			this.value = value;
		}
		int value;
		
		public String toString() {
			return Integer.toString(value);
		}
	}
	
	private String endpoint;
	
	private final Map<String, File> inProcessingFiles = new HashMap<String, File>();
	private final Map<String, IntHolder> fileUsage = new HashMap<String, IntHolder>();
	private final Map<String, IntHolder> fileActionUsage = new HashMap<String, IntHolder>();
	
	public FileManager() {
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String toString() {
		synchronized(fileUsage) {
			return String.valueOf(fileUsage);
		}
	}
	
	public File get(ActionContext context, File destFile) throws Exception {
		synchronized(fileUsage) {
			File file = inProcessingFiles.get(context.getPath());
			if(file == null || !file.exists()) {
				file = FileHelper.download(destFile, getDownloadPath(context), context.getToken());
				InputStream input = new FileInputStream(file);
	            String etag = DigestUtils.sha256Hex(input);
	            setEtag(context.getPath(), etag);
	            IOUtils.closeQuietly(input);	            
	            LOGGER.info(context.getPath() + " downloaded to " + file.getCanonicalPath() + ", etag: " + etag);
	            statisticsHandler.incFileDownloaded();
			}
			addReference(context, file);
			return file;
		}		
	}
	
	public int addReference(ActionContext context, File file) {
		synchronized(fileUsage) {
			String path = context.getPath();
			IntHolder ih = fileUsage.get(path);
			if(ih != null) {
				ih.value ++;
			} else {
				ih = new IntHolder(1);
				inProcessingFiles.put(path,  file);
				fileUsage.put(path, ih);
			}
			String actionPathKey = context.getAction() + ":" + context.getPath();
			IntHolder ih2 = fileActionUsage.get(actionPathKey);
			if(ih2 != null) {
				ih2.value ++;
			} else {
				ih2 = new IntHolder(1);
				fileActionUsage.put(actionPathKey, ih2);
			}
			return ih.value;
		}
	}
	
	public int removeReference(ActionContext context) {
		synchronized(fileUsage) {
			String path = context.getPath();
			
			String actionPathKey = context.getAction() + ":" + context.getPath();
			IntHolder ih2 = fileActionUsage.get(actionPathKey);
			if(ih2 != null) {
				ih2.value --;
				if(ih2.value == 0) fileActionUsage.remove(actionPathKey);
			}
			
			IntHolder ih = fileUsage.get(path);
			if(ih != null) {
				ih.value --;
				if(ih.value == 0) {
					fileUsage.remove(path);
					File file = inProcessingFiles.remove(path);
					if(file != null) {
						FileHelper.deleteFile(file);
					}
				}
				return ih.value;
			}
		}
		return 0;
	}
	
	public int getFileBasedRefCount(ActionContext context) {
		synchronized(fileUsage) {
			IntHolder ih = fileUsage.get(context.getPath());
			if(ih != null) return ih.value;
			return 0;
		}
	}
	
	public int getActionBasedRefCount(ActionContext context) {
		synchronized(fileActionUsage) {
			String actionPathKey = context.getAction() + ":" + context.getPath();
			IntHolder ih = fileActionUsage.get(actionPathKey);
			if(ih != null) return ih.value;
			return 0;
		}
	}
	
	public String getEtag(String path) {
		return etagCache.getProperty(path);
	}
	
	public void setEtag(String path, String etag) {
		if(etag == null) etagCache.remove(path);
		else etagCache.setProperty(path, etag);
		statisticsHandler.setEtagCacheSize(etagCache.size());
	}
	
	private String getDownloadPath(ActionContext context) {
        StringBuilder buf = new StringBuilder();
        buf.append(context.getServer()).append(endpoint).append(context.getPath());
        return buf.toString();
	}
 
	@Override
	public void destroy() throws Exception {
		File file = new File(CACHE_FILE_NAME);
		etagCache.store(new FileOutputStream(file), "Etag Cache");
		LOGGER.info("Etag cache saved: " + etagCache.size());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("File rawdata endpoint: " + endpoint);
		File file = new File(CACHE_FILE_NAME);
		if(file.exists()) {
			try {
				etagCache.load(new FileInputStream(file));
				LOGGER.info("Etag cache loaded: " + etagCache.size());
				statisticsHandler.setEtagCacheSize(etagCache.size());
			} catch (FileNotFoundException e) {
				LOGGER.error("Can not load etag cache from " + file.getName());
			} catch (IOException e) {
				LOGGER.error("Can not load etag cache from " + file.getName());
			}
		}
	}	
	
	public static void main(String[] args) throws Exception {
		FileManager fm = new FileManager();
		fm.setEndpoint("");
		fm.setStatisticsHandler(new StatisticsHandler());
		fm.afterPropertiesSet();
		Iterator<Object> it = fm.etagCache.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			System.out.println(key + ": " + fm.etagCache.get(key));
		}
	}
}
