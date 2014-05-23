/**
 * 
 */
package com.alternativagame.resource.utils.psd.types;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class VString {

    private String value;

    /**
     * @param in
     * @throws IOException
     */
    public VString(DataInputStream in) throws IOException {
	int size = in.readInt() * 2;
	if (size > 0) {
	    byte data[] = new byte[size];
	    in.read(data);
	    value = new String(data, "utf-16");
	} else
	    value = "";
    }

    public String getValue() {
	return value;
    }

    @Override
    public String toString() {
	return value;
    }

}
