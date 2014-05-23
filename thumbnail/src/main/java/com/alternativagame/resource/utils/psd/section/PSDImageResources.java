/**
 * 
 */
package com.alternativagame.resource.utils.psd.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alternativagame.resource.utils.psd.IRBConstants;
import com.alternativagame.resource.utils.psd.irb.SlicesIRB;
import com.alternativagame.resource.utils.psd.types.B4String;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class PSDImageResources {

    private final Map<Integer, Object> irbs = new HashMap<Integer, Object>();

    public PSDImageResources(DataInputStream in) throws IOException {
	int size = in.readInt();
	int readedSize = 0;
	while (readedSize < size) {
	    // ostype
	    readedSize += 4;
	    B4String osType = new B4String(in);
	    // ������� ������ ��� ���, �������� ���� ������ ���������� �������
	    // �����
	    assert osType.getValue().equals("8BIM");

	    // id
	    int id = in.readUnsignedShort();
	    readedSize += 2;
	    // name
	    int length = in.readUnsignedByte();
	    readedSize++;
	    // ����� �� ��������
	    length = (length % 2) == 0 ? 1 : length;
	    readedSize += length;
	    byte[] name_byte = new byte[length];
	    in.read(name_byte);
	    String name = new String(name_byte);

	    // ������ IRB
	    int sizeIRB = in.readInt();
	    readedSize += 4;
	    // ������ ���������� IRB
	    makeIRB(sizeIRB, id, in);
	    // �������� ������
	    readedSize += sizeIRB;
	    // ����� �� ��������
	    if (sizeIRB % 2 == 1) {
		if (in.available() >= 1) {
		    in.readByte();
		    readedSize++;
		}
	    }

	}
    }

    /**
     * ������������ IRB ����
     * 
     * @param id
     * @param in
     * @throws IOException
     */
    private void makeIRB(int size, int id, DataInputStream in) throws IOException {
	int current = in.available();
	switch (id) {
	case IRBConstants.SLICES:
	    irbs.put(IRBConstants.SLICES, new SlicesIRB(in));
	    break;

	default:
	    in.skipBytes(size);
	    break;
	}

	// ����������, ���� ����� �� ������� ��� ��� ��������
	in.skip(in.available() - (current - size));
    }

    /**
     * �������� IRB ������
     * 
     * @param type
     * @return
     */
    public Object getIRB(int type) {
	assert irbs.containsKey(type);
	return irbs.get(type);
    }
}
