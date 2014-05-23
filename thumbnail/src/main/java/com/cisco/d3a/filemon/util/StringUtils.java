package com.cisco.d3a.filemon.util;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * User: haihxiao
 * Date: 11/13/12
 * Time: 2:31 PM
 */
public class StringUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
	public static Map parseJSON(String jsonString) {
        try {
            return mapper.readValue(jsonString, Map.class);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(Date date) {
    	return DF.format(date);
    }
    
    static final DateFormat DF = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
