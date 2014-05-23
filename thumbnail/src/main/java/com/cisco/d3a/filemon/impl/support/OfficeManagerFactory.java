package com.cisco.d3a.filemon.impl.support;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class OfficeManagerFactory implements FactoryBean<OfficeManager>, InitializingBean {
	private String host;
	private int port;
		
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public OfficeManager getObject() throws Exception {
		DefaultOfficeManagerConfiguration omc = new DefaultOfficeManagerConfiguration();
	    omc.setConnectionProtocol(OfficeConnectionProtocol.SOCKET);
	    omc.setPortNumber(port);

	    OfficeManager officeManager = omc.buildOfficeManager();
	    officeManager.start();		
		return officeManager;
	}

	@Override
	public Class<?> getObjectType() {
		return OfficeManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
