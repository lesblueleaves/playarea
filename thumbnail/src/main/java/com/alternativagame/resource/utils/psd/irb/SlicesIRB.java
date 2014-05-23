/**
 * 
 */
package com.alternativagame.resource.utils.psd.irb;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alternativagame.resource.utils.psd.types.Rect;
import com.alternativagame.resource.utils.psd.types.VString;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class SlicesIRB {

    private Rect bounding;

    private VString name;

    private int count;

    private final List<Slice> slices = new ArrayList<Slice>();

    public SlicesIRB(DataInputStream in) throws IOException {

	// ������ ���������
	parseHeader(in);
	// ������ ������
	for (int i = 0; i < count; i++) {
	    Slice slice = new Slice(in);
	    this.slices.add(slice);
	}

    }

    private void parseHeader(DataInputStream in) throws IOException {
	// ������
	int version = in.readInt();
	// BoundingBox
	bounding = new Rect(in);
	// ������������
	name = new VString(in);
	// ���������� �������
	count = in.readInt();

    }

    /**
     * �������� ������ �������
     * 
     * @return
     */
    public List<Slice> getSlices() {
	return slices;
    }

}
