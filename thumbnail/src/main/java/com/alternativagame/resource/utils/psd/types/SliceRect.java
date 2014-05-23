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
public class SliceRect extends Rect {

    /**
     * @param in
     * @throws IOException
     */
    public SliceRect(DataInputStream in) throws IOException {

	setLeft(in.readInt());
	setTop(in.readInt());
	setRight(in.readInt());
	setBottom(in.readInt());
    }

}
