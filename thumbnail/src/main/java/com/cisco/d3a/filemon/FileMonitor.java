package com.cisco.d3a.filemon;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.api.ActionContext;
import com.cisco.d3a.filemon.api.FileAction;
import com.cisco.d3a.filemon.api.FileProcessor;
import com.cisco.d3a.filemon.api.LocalStorage;
import com.cisco.d3a.filemon.util.FileManager;

/**
 *
 */
public class FileMonitor implements MessageListener, InitializingBean {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    static final String TOKEN = "__token__";
    static final String FROM = "__from__";
    static final String USER = "__user__";
    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String ETAG = "etag";
    private static final String ACTION = "action";
    private static final String TIME = "time";
    
    private Map<String, List<FileProcessor>> processors;
	private ExecutorService executorService;
	private BlockingQueue<Runnable> blockingQueue;
	private StatisticsHandler statisticsHandler;
	
	private LocalStorage localStorage;
	
	private FileManager fileManager;
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public StatisticsHandler getStatisticsHandler() {
		return statisticsHandler;
	}

	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	public void setLocalStorage(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}
		
	public Map<String, List<FileProcessor>> getProcessors() {
		return processors;
	}

	public void setProcessors(Map<String, List<FileProcessor>> processors) {
		this.processors = processors;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public BlockingQueue<Runnable> getBlockingQueue() {
		return blockingQueue;
	}

	public void setBlockingQueue(BlockingQueue<Runnable> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	@Override
	public void onMessage(Message msg) {
		try {
	    	MapMessage message = (MapMessage)msg;
            final String user = message.getStringProperty(USER);
            if(user == null) {
                LOGGER.warn("User is not specified in request");
            	return;
            }

	        final String action = message.getString(ACTION);
            FileAction fAction = FileAction.fromString(action);
            if(fAction == null) {
                LOGGER.warn("Unknown action: ", action);
                return;
            }
            
            final String name = message.getString(NAME);
            final String path = message.getString(PATH);
            final String etag = message.getString(ETAG);
            final long time = message.getLong(TIME);
            final String from = message.getStringProperty(FROM);
            final String token = message.getStringProperty(TOKEN);

	        ActionContext context = new ActionContext(fAction, path, from, user, token, etag, new Date(time));
	        System.out.println("===============================================================================================");
            System.out.println("| name: " + name);
            System.out.println("| path: " + context.getPath());
            System.out.println("| etag: " + context.getEtag());
            System.out.println("| from: " + context.getServer());
            System.out.println("| time: " + com.cisco.d3a.filemon.util.StringUtils.format(context.getTime()));
            System.out.println("| user: " + context.getUser() + ":" + StringUtils.abbreviate(context.getToken(), 40) + "(" + context.getToken().length() + ")");
            System.out.println("| actn: " + action);
            
			List<FileProcessor> theProcessors = processors.get(context.getAction().action());
			if(theProcessors == null) {
				LOGGER.warn("No processor defined for " + context.getAction().action());
			} else {
		        onMessage(context, theProcessors);
			}
		} catch(Exception e) {
            LOGGER.error("Error in receiving message: ", e);
		}
	}
	
	private void onMessage(final ActionContext context, final List<FileProcessor> theProcessors) throws Exception {  
		final String action = context.getAction().action();
		if(LOGGER.isInfoEnabled()) {
			StringBuilder buf = new StringBuilder();
			buf.append(context.getUser()).append("|").append(context.getServer());
			buf.append("|").append(action).append("|").append(context.getPath());
	        LOGGER.info(buf.toString());
		}
		statisticsHandler.incFileQueued(1);
		executorService.execute(new Runnable() {
			public void run() {
				boolean inProcessing = false;					
				long before = System.currentTimeMillis();
				try {
					File destFile = null;
					if(context.getAction().isFileRequired()) {
                        destFile = fileManager.get(context, localStorage.getLocalFile(context.getPath(), false));                            
			            if(destFile.length() == 0) {
			            	LOGGER.info(context.getPath() + " has no content inside.");
			            	return;
			            }
						String etag = fileManager.getEtag(context.getPath());
						if((context.getAction() == FileAction.EDIT_FILE || context.getAction() == FileAction.EDIT) && etag != null && etag.equals(context.getEtag())) {
							LOGGER.info("File " + context.getPath() + " is not modified, ignore further processing");
							return;
						}		
						context.setEtag(etag);
					} else {
						fileManager.addReference(context, localStorage.getLocalFile(context.getPath(), false));
						destFile = null;
					}
					int refCount = fileManager.getActionBasedRefCount(context);
					inProcessing = refCount > 1;
					if(inProcessing) {
						LOGGER.info("File " + context.getPath() + " is in processing(" + refCount + ")");
						return;
					}
					
					for(FileProcessor processor : theProcessors) {
        				try {
    						processor.onModified(destFile, context);
        				} catch (Exception e) {
        					LOGGER.error(e.getMessage(), e);
        				}
					}						
					statisticsHandler.incFileProcessed();
				} catch(Exception e) {
					statisticsHandler.incErrorCount();
					LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
				} finally {
					statisticsHandler.decFileQueued(1);
					long ms = System.currentTimeMillis() - before;
					int refCount = fileManager.removeReference(context);
					if(!inProcessing) {
						LOGGER.info(context.getPath() + " processed(" + refCount + ") in " + ms + " ms(" + statisticsHandler.getFileProcessed() + ")");						
					}
					LOGGER.info(context.getPath() + " processed - " + fileManager);						
				}
			}
		});
	}
}
