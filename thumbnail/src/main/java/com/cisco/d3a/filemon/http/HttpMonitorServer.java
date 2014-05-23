package com.cisco.d3a.filemon.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.StatisticsHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HttpMonitorServer implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpMonitorServer.class);

	private StatisticsHandler statisticsHandler;
	private int port;
		
	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	class StatusHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {			
			String response = statisticsHandler.toJSON();
			t.getResponseHeaders().add("content-type", "application/json");
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
		server.createContext("/", new StatusHandler());
		server.setExecutor(null);
		server.start();
		LOGGER.info("http server is listening on " + port);
	}
}
