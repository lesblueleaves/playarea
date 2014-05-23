package com.cisco.d3a.filemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.d3a.filemon.util.FileHelper;
import com.cisco.d3a.filemon.util.UrlUtils;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class FileTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileTest.class);

	public static void main(String[] args) {

		 String
		 fileUrl="F:\\sourcecode\\springroll\\trunk\\Src\\Server\\springroll-server\\thumbnail\\data\\local\\huiswang@cisco.com\\m2\\Aggregation for large site.xps";
//		String fileUrl = "d:\\files\\tx.txt";
		System.out.println(UrlUtils.encodeUrl(fileUrl));
//		File file = new File(fileUrl);
		File out = new File(fileUrl);
		String url="http://10.140.92.115:8080/rollservice/api/file/data/huiswang@cisco.com/m2/Aggregation for large site.xps";
		try {

//			download(
//					out,
//					"http://10.140.92.115:8080/rollservice/api/file/data/huiswang@cisco.com/m1/Aggregation for large site.xps",
//					"");
			
			FileHelper.download(out, url, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			testCopy();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void testSRCopy(){
		
	}

	public static void testCopy() throws Exception {
		
		String urlString="http://10.140.92.115:8080/rollservice/api/file/data/huiswang@cisco.com/m1/t1.txt";
//		File in = new File("http://10.140.92.115:8080/rollservice/api/file/data/huiswang@cisco.com/m1/t1.txt");
		
		
		URL url = new URL(UrlUtils.encodeUrl(urlString));
		URLConnection urlc = url.openConnection();
		
		try {
		File out = new File("D:\\files\\2.txt");
		boolean threw = true;
			FileOutputStream outStream = new FileOutputStream(out);
			final InputStream in =urlc.getInputStream();
			try {
//				ByteStreams.copy(urlc.getInputStream(), outStream);
				Files.copy(new InputSupplier<InputStream>() {
					public InputStream getInput() {
						return in;
					}
				}, out);
				
				threw = false;
			} finally {
				Closeables.close(outStream, threw);
			}
		} finally {
//			Closeables.close(inStream, threw);
		}
	}

	public static File download(File to, String urlString, String authToken)
			throws Exception {
		LOGGER.info(" download urlString: " + urlString + " authToken:"
				+ authToken);
		URL url = new URL(UrlUtils.encodeUrl(urlString));
		URLConnection urlc = url.openConnection();
		// urlc.setRequestProperty("Authorization", "Basic " + authToken);
		return download(to, urlc.getInputStream());
	}

	public static File download(File to, final InputStream in) throws Exception {
		Files.copy(new InputSupplier<InputStream>() {
			public InputStream getInput() {
				return in;
			}
		}, to);
		// LOGGER.trace(to.getCanonicalPath() + " downloaded");
		LOGGER.info(to.getCanonicalPath() + " downloaded");
		return to;
	}
}
