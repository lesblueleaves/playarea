package com.cisco.d3a.filemon.http;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.apache.pdfbox.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServerTest {
	static private String urlPrefix="/file";
	static private String urlRoot ="F:\\sourcecode\\springroll\\trunk\\Src\\Server\\springroll-server\\thumbnail\\data\\root";
	static private File docRoot;
	static private FileNameMap fileNameMap = URLConnection.getFileNameMap();  
	
	public static void main(String[] args) throws IOException {
		 HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/test", new MyHandler());
	        server.createContext("/file", new FileHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
	}
	
	static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	String response = t.getRequestURI().toString();
//            String response = "This is the response";
        	
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
	static class FileHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response = t.getRequestURI().toString();
			System.out.println("response:"+response);
			if(response.startsWith(urlPrefix)) {
				
				docRoot = new File(urlRoot);
				
				String relPath = response.substring(urlPrefix.length());
//				relPath = relPath.replaceAll("%20", " ");
				relPath = URLDecoder.decode(relPath, "UTF-8");
				System.out.println("relPath:"+relPath);
				
				File file = new File(docRoot, relPath);
				System.out.println("file:"+file.getAbsolutePath());
				
				if(file.isFile()) {
					System.out.println("file:"+file.getAbsolutePath());
				    String type = fileNameMap.getContentTypeFor(file.getCanonicalPath());  
				    System.out.println("type: "+type);
//					String type = "jepg/png";
					t.getResponseHeaders().add("content-type", type);
					t.sendResponseHeaders(200, file.length());
					OutputStream os = t.getResponseBody();
					IOUtils.copy(new FileInputStream(file), os);
					os.close();
				} else {
					t.sendResponseHeaders(404, 0);
				}
			} else {
				t.sendResponseHeaders(404, 0);
			}
		}
	}


}
