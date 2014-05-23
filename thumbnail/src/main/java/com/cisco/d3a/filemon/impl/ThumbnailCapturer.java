package com.cisco.d3a.filemon.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.StatisticsHandler;
import com.cisco.d3a.filemon.api.ActionContext;
import com.cisco.d3a.filemon.api.CaptureSpec;
import com.cisco.d3a.filemon.api.FileProcessor;
import com.cisco.d3a.filemon.api.ImageCapturer;
import com.cisco.d3a.filemon.api.LocalStorage;
import com.cisco.d3a.filemon.impl.img.ImageCapturerManager;
import com.cisco.d3a.filemon.impl.support.ImageResizer;
import com.cisco.d3a.filemon.util.FileHelper;
import com.cisco.d3a.filemon.util.HttpClientFactory;
import com.google.common.base.Joiner;

public class ThumbnailCapturer implements FileProcessor, InitializingBean, DisposableBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailCapturer.class);

	private ImageCapturerManager imageCapturerManager;
    private CaptureSpec captureSpec;
    private List<CaptureSpec> resizeSpecs;
    private LocalStorage localStorage;
	private String endpoint;
	private StatisticsHandler statisticsHandler;
	private String urlPrefix;
	
	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
	
	public void setLocalStorage(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setImageCapturerManager(ImageCapturerManager imageCapturerManager) {
		this.imageCapturerManager = imageCapturerManager;
	}

    public void setCaptureSpec(CaptureSpec captureSpec) {
        this.captureSpec = captureSpec;
    }

    public void setResizeSpecs(List<CaptureSpec> resizeSpecs) {
        this.resizeSpecs = resizeSpecs;
    }

    public void onModified(File file, ActionContext context) throws Exception {
		final String fullPath = context.getPath();
		
		String ext = FilenameUtils.getExtension(fullPath);

		long before = System.currentTimeMillis();
		ImageCapturer imageCapture = imageCapturerManager.getImageCapturer(ext);
		if(imageCapture == null) {
			LOGGER.debug("Unsupported file type: " + ext);
			return;
		}
		
        File imageFolder = localStorage.getTempFile(context.getPath(), true);
		
        File tImageFolder = new File(imageFolder, captureSpec.getPath());
        tImageFolder.mkdirs();
        File thumbFolder = localStorage.getThumbnailFile(context.getPath(), true);
        boolean thumbnailGenerated = false;
        try {
			LOGGER.info("Capture thumbnails for " + context.getPath());           
			imageCapture.capture(file, tImageFolder, captureSpec);
            thumbnailGenerated = true;
        } catch (IOException e) {
            LOGGER.error("Error in capturing thumbnail for " + fullPath, e);
        } catch (Exception e) {
            LOGGER.error("Error in capturing thumbnail for " + fullPath, e);
        }

		if(thumbnailGenerated) {
            List<File> thumbnailFiles = FileHelper.sort(file, tImageFolder, new FileFilter() {
    			@Override
    			public boolean accept(File pathname) {
    				return pathname.getName().endsWith(captureSpec.getType());
    			}        	
            });
            
            if(thumbnailFiles.size() != 0) {
            	statisticsHandler.incFileCaptured(1);
                resizeThumbnails(file, thumbFolder, thumbnailFiles, context);
                LOGGER.info("Thumbnail " + fullPath + " captured in " + (System.currentTimeMillis() - before) + " ms.");
            } else {
                LOGGER.info("No thumbnails captured for " + fullPath);
            }

            try {
				uploadThumbnails(file, thumbFolder, context);
			} catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
//				FileHelper.deleteFile(imageFolder);
            }
		}
	}

    private void resizeThumbnails(File file, File to, List<File> thumbnailFiles, ActionContext context) {
        final int numberOfImages = thumbnailFiles.size();
        // Delete old thumbnail images
        for(CaptureSpec resizeSpec : resizeSpecs) {
            File dest = new File(to, resizeSpec.getPath());
            try {
				FileUtils.deleteDirectory(dest);
			} catch (IOException e) {
			}
        }        
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
                        } else {
                        	LOGGER.warn("Illegal thumbnail size: " + size);
                        }
                    }
                }
            } catch(IOException e) {
                LOGGER.error("Error in resizing thumbnail for " + thumbnailFiles.get(i).getAbsolutePath(), e);
            }
        }
    }

    private void uploadThumbnails(File file, File thumbnailFolder, ActionContext context) throws Exception {
		if(thumbnailFolder.exists() && thumbnailFolder.isDirectory()) {
			LOGGER.debug("Uploading thumbnails under " + thumbnailFolder.getCanonicalPath());
			
			HttpClient client = HttpClientFactory.createHttpClient();
			try {
				int c = 0;
	            JSONObject thumbnails = new JSONObject();
	            for(CaptureSpec resizeSpec : resizeSpecs) {
	            	 File t = new File(thumbnailFolder, resizeSpec.getPath());
	                List<String> collector = new ArrayList<String>();
	                c += collectThumbnailsForSpec(context, t, resizeSpec, collector);
	                thumbnails.put(resizeSpec.getPath(), Joiner.on("|").join(collector.iterator()));
	            }

	            // upload thumbnails stat info
	            if(c != 0) {
		            if(c != 0) LOGGER.info(thumbnailFolder.getName() + " collected " + c + " thumbnails");

		            HttpPost post = new HttpPost(context.getServiceLocation(endpoint));
	                post.addHeader("Authorization", "Basic " + context.getToken());

	                JSONObject jsonObject = new JSONObject().put("thumbnail", thumbnails);
	                StringEntity entity = new StringEntity(jsonObject.toString(), ContentType.create("application/json", "UTF-8"));
	                post.setEntity(entity);

	                LOGGER.debug("Thumbnail req: " + jsonObject.toString());
	                HttpResponse response = client.execute(post);
	                String status = EntityUtils.toString( response.getEntity(), "UTF-8" );
	                if(response.getStatusLine().getStatusCode() == 200) {
	                    LOGGER.info("Thumbnail data for " + context.getPath() + " -> " + StringUtils.abbreviate(status, 40) + "(" + status.length() + ")");
	                } else {
	                    LOGGER.warn("Thumbnail data for " + context.getPath() + " -> " + status);
	                }
	            } else {
	            	// delete deprecated thumbnails info
					HttpDelete deleteMethod = new HttpDelete(context.getServiceLocation(endpoint));
					deleteMethod.addHeader("Authorization", "Basic " + context.getToken());
					HttpResponse response = client.execute(deleteMethod);
					LOGGER.debug(context.getPath() + " deleted thumbnails: " + EntityUtils.toString( response.getEntity(), "UTF-8" ));
					
	            }
	            statisticsHandler.incThumbnailCaptured(c);
			} finally {
				client.getConnectionManager().shutdown();		
			}
        } else {
			LOGGER.warn("Invalid thumbnail folder: " + thumbnailFolder.getName() + " for " + context.getPath());
		}
	}
	
	private int collectThumbnailsForSpec(ActionContext context, File folder, final CaptureSpec resizeSpec, List<String> collector) throws Exception {
		int count = 0;	
        List<File> thumbnails = FileHelper.sort(null, folder, new FileFilter() {
        	@Override
        	public boolean accept(File filename) {
        		return filename.getName().endsWith(resizeSpec.getType()); // same image suffix
        	}
        });
		for(int i=0;thumbnails != null && i<thumbnails.size();i++) {
			File t = thumbnails.get(i);
			StringBuilder fullPath = new StringBuilder();
			fullPath.append(this.urlPrefix);
			fullPath.append("/");
			fullPath.append(context.getPath());
			fullPath.append("/");
			fullPath.append(resizeSpec.getPath());
			fullPath.append("/");
			fullPath.append(t.getName());			
			collector.add(fullPath.toString());
			count ++;
		}
		return count;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for(CaptureSpec resizeSpec : this.resizeSpecs) {
			LOGGER.info("ResizeSpec " + resizeSpec);
		}
		LOGGER.info("File thumbnail endpoint: " + endpoint);
		LOGGER.info("File thumbnail url prefix: " + urlPrefix);
	}
	
	@Override
	public void destroy() throws Exception {
	}
}
