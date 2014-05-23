package com.cisco.d3a.filemon.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cisco.d3a.filemon.util.UrlUtils;

public class ActionContext {
    private String path;
    private FileAction action;
    private String server;
    private String user;
    private String token;
    private String etag;
    private Map<String, String> properties = new HashMap<String, String>();
    private Date time;
    
	public ActionContext(FileAction action, String path, String server, String user, String token, String etag, Date time) {
		this.action = action;
		this.path = path;
        this.server = server;
        this.user = user;
        this.etag = etag;
        this.token = token.replaceAll("[\\r\\n]", "");
        this.time = time;
	}
	
	public String getPath() {
		return path;
	}

	public FileAction getAction() {
		return action;
	}

	public String getUser() {
		return user;
	}

	public String getEtag() {
		return etag;
	}
	
    public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getServer() {
        return server;
    }

    public String getToken() {
		return token;
	}	
    
	public Date getTime() {
		return time;
	}

	public void setProperty(String name, String value) {
		if(value == null) this.properties.remove(name);
		else this.properties.put(name, value);
	}
	
	public String getProperty(String name) {
		return this.properties.get(name);
	}
	
	public Map<String, String> getProperties() {
		return this.properties;
	}
	
	public boolean hasProperty(String name) {
		return this.properties.containsKey(name);
	}
	
	public String getServiceLocation(String endpoint) {
		StringBuilder buf = new StringBuilder();
		buf.append(getServer()).append(endpoint).append(getPath());
        return UrlUtils.encodeUrl(buf.toString());
	}
}
