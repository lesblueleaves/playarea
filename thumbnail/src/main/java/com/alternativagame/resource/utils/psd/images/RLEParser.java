/**
 * 
 */
package com.alternativagame.resource.utils.psd.images;

import java.io.DataInputStream;
import java.io.IOException;

import com.alternativagame.resource.utils.psd.images.unpacker.RLEPackBits;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class RLEParser {

    byte[] result = null;

    /**
     * @param bounds
     * @param in
     * @throws IOException
     */
    public RLEParser(int width, int heigth, DataInputStream in) throws IOException {

	result = new byte[width * heigth];

	int[] sizeScanLines = new int[heigth];

	int allSize = 0;
	for (int i = 0; i < heigth; i++) {
	    sizeScanLines[i] = in.readUnsignedShort();
	    allSize += sizeScanLines[i];
	}

	for (int i = 0; i < heigth; i++) {
	    int sizeScanLine = sizeScanLines[i];
	    byte[] data = new byte[sizeScanLine];
	    in.read(data);
	    RLEPackBits packBits = new RLEPackBits(data, width);
	    byte[] line = packBits.unpack();
	    System.arraycopy(line, 0, result, i * width, line.length);
	}

    }

    /**
     *
     * @return
     */
    public byte[] getData() {
	return result;
    }
}
