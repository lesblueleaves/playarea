package com.cisco.d3a.filemon.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.StatisticsHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class WebServer implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
	private FileNameMap fileNameMap = URLConnection.getFileNameMap();  
	
	private StatisticsHandler statisticsHandler;
	private int port;
	private File docRoot;
	private String urlPrefix;
	private boolean index;
	private Comparator<File> nameComparator;
	
	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIndex(boolean index) {
		this.index = index;
	}

	public void setDocRoot(String docRoot) {
		this.docRoot = new File(docRoot);
		if(!this.docRoot.isDirectory()) {
			throw new IllegalArgumentException(docRoot + " is not a folder");
		}
	}
	
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = "/" + urlPrefix;
	}

	class StatusHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {			
			String response = statisticsHandler.toJSON();
			t.getResponseHeaders().add("content-type", "application/json");
			t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	class FileHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response = t.getRequestURI().toString();
			if(response.startsWith(urlPrefix) && !response.contains("..")) {
				String relPath = response.substring(urlPrefix.length());	
				relPath = URLDecoder.decode(relPath, "UTF-8");
				File file = new File(docRoot, relPath);
				if(file.isFile()) {
				    String type = fileNameMap.getContentTypeFor(file.getCanonicalPath());  
				    if(type == null) type = "application/octect-stream";
					t.getResponseHeaders().add("content-type", type);
					t.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
					OutputStream os = t.getResponseBody();
					IOUtils.copy(new FileInputStream(file), os);
					IOUtils.closeQuietly(os);
					return;
				}
				if(file.isDirectory()) {
					if(index) {
						OutputStream os = t.getResponseBody();
						byte[] content = outputDirectoryList(response, file);
						t.getResponseHeaders().add("content-type", "text/html");
						t.sendResponseHeaders(HttpURLConnection.HTTP_OK, content.length);
						os.write(content);
						IOUtils.closeQuietly(os);
					} else {
						t.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
						OutputStream os = t.getResponseBody();
						os.write("<h1>403 Forbidden</h1>".getBytes());
						IOUtils.closeQuietly(os);
					}
					return;
				}
			}
			t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
			OutputStream os = t.getResponseBody();
			os.write("<h1>404 Not Found</h1>".getBytes());
			IOUtils.closeQuietly(os);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
		server.createContext("/status", new StatusHandler());
		server.createContext(urlPrefix, new FileHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		nameComparator = new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		};
		LOGGER.info("WebServer is listening on " + port);
	}
	
	private byte[] outputDirectoryList(String url, File folder) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter printer = new PrintWriter(baos);
		printer.print("<h1>");
		printer.print("Index of /" + folder.getName());
		printer.println("</h1>");
		printer.println("<table border=\"0\" style=\"min-width:940px;\"><thead><tr style=\"font-size: 120%;font-weight: bold;\"><td>Name</td><td>Type</td><td>Size</td><td>Last Modified</td></tr></thead><tbody>");
		List<File> folders = Arrays.asList(folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}	
		}));
		Collections.sort(folders, nameComparator);
		List<File> files = Arrays.asList(folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}	
		}));
		Collections.sort(files, nameComparator);
		for(File file : folders) {
			printer.print("<tr>");
			printer.print("<td>" + "<a href=\"" + url + file.getName() + "/\">" + file.getName() + "</a>" + "</td>");
			printer.print("<td> - </td>");
			printer.print("<td>&nbsp;</td>");
			printer.print("<td>" + new Date(file.lastModified()) + "</td>");
			printer.println("</tr>");
		}
		for(File file : files) {
			printer.print("<tr>");
			printer.print("<td>" + "<a href=\"" + url + file.getName() + "\">" + file.getName() + "</a>" + "</td>");
		    String type = fileNameMap.getContentTypeFor(file.getCanonicalPath());  
			printer.print("<td>" + (type == null ? "&nbsp;" : type) + "</td>");
			printer.print("<td>" + file.length() + "</td>");
			printer.print("<td>" + new Date(file.lastModified()) + "</td>");
			printer.println("</tr>");
		}
		printer.print("<tfoot><tr style=\"font-size: 90%;font-style:italic;\"><td colspan=\"4\">");
		printer.print(folders.size() + " folder(s), " + files.size() + " file(s)");
		printer.println("</td></tr></tfoot></tbody></table>");
		printer.close();
		return baos.toByteArray();
	}
}
