package com.cisco.d3a.filemon.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class UrlUtils {
	protected static final char[] hexadecimal = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	// Array containing the safe characters set.
	protected BitSet safeCharacters = new BitSet(256);

	public UrlUtils() {
		for (char i = 'a'; i <= 'z'; i++) {
			addSafeCharacter(i);
		}
		for (char i = 'A'; i <= 'Z'; i++) {
			addSafeCharacter(i);
		}
		for (char i = '0'; i <= '9'; i++) {
			addSafeCharacter(i);
		}
	}

	public void addSafeCharacter(char c) {
		safeCharacters.set(c);
	}

	public String encode(String path) {
		int maxBytesPerChar = 10;
		int caseDiff = ('a' - 'A');
		StringBuffer rewrittenPath = new StringBuffer(path.length());
		ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(buf, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
			writer = new OutputStreamWriter(buf);
		}

		for (int i = 0; i < path.length(); i++) {
			int c = (int) path.charAt(i);
			if (safeCharacters.get(c)) {
				rewrittenPath.append((char) c);
			} else {
				// convert to external encoding before hex conversion
				try {
					writer.write((char) c);
					writer.flush();
				} catch (IOException e) {
					buf.reset();
					continue;
				}
				byte[] ba = buf.toByteArray();
				for (int j = 0; j < ba.length; j++) {
					// Converting each byte in the buffer
					byte toEncode = ba[j];
					rewrittenPath.append('%');
					int low = (int) (toEncode & 0x0f);
					int high = (int) ((toEncode & 0xf0) >> 4);
					rewrittenPath.append(hexadecimal[high]);
					rewrittenPath.append(hexadecimal[low]);
				}
				buf.reset();
			}
		}
		return rewrittenPath.toString();
	}
    
    public static String encodeUrl(String url) {
    	try {
        	final URL l = new URL(url);    
        	final URI uri = new URI(l.getProtocol(), null, l.getHost(), l.getPort(), l.getPath(), l.getQuery(), null);
        	return escapeUrl(uri.toASCIIString());
    	} catch(Exception e) {
    		return url;
    	}
    }
        
    private static String escapeUrl(String url) {
    	for(String key : ESCAPABLE_CHARS.keySet()) {
    		url = url.replaceAll(key, ESCAPABLE_CHARS.get(key));
    	}
    	return url;
    }
        
    private static final UrlUtils ENC = new UrlUtils();
    static {
    	ENC.addSafeCharacter('/');
    	ENC.addSafeCharacter('@');
    	ENC.addSafeCharacter('.');
    }
    
    private static final Map<String, String> ESCAPABLE_CHARS = new HashMap<String, String>();
    static {
    	ESCAPABLE_CHARS.put("\\+", "%2B");
    }
    
    public static void main(String[] args) throws Exception {
    	String url = "http://10.140.29.76:8080/d3a/api/file/data/haihxiao@cisco.com/C++ 中文版 CWIC_API.pdf";
    	System.out.println(encodeUrl(url));
    }
}
