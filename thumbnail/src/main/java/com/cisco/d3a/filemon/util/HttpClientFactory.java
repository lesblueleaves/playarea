package com.cisco.d3a.filemon.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.springframework.beans.factory.FactoryBean;

public class HttpClientFactory implements FactoryBean<HttpClient> {
	public static final String userAgent = "D3AFileMonitor/1.0";

	public static HttpClient createHttpClient() {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);		
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);		
		return client;
	}

	@Override
	public HttpClient getObject() throws Exception {
		return createHttpClient();
	}

	@Override
	public Class<?> getObjectType() {
		return HttpClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
