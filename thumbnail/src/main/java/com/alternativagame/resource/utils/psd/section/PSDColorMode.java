/**
 * 
 */
package com.alternativagame.resource.utils.psd.section;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class PSDColorMode {

    public PSDColorMode(DataInputStream in) throws IOException {
	int size = in.readInt();
	in.skipBytes(size);
    }

}
