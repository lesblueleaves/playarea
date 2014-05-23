/**
 * 
 */
package com.alternativagame.resource.utils.psd.layer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alternativagame.resource.utils.psd.types.B4String;
import com.alternativagame.resource.utils.psd.types.PString;
import com.alternativagame.resource.utils.psd.types.Rect;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class PSDLayerStructure {

    // bounding box
    private final Rect bounds;

    // ���������� �������
    private final short numberChannels;

    // ���������� � �������
    private final List<ChannelLengthInfo> channelsLengthInfo = new ArrayList<ChannelLengthInfo>();

    // ���������
    private final B4String signature;

    private final B4String blendModeKey;

    private final byte opacity;

    private final byte clipping;

    private final byte flags;

    private final PString name;

    /**
     * @param in
     * @throws IOException
     */
    public PSDLayerStructure(DataInputStream in) throws IOException {

	this.bounds = new Rect(in);
	this.numberChannels = in.readShort();
	for (int i = 0; i < numberChannels; i++) {
	    ChannelLengthInfo info = new ChannelLengthInfo(in);
	    channelsLengthInfo.add(info);
	}

	this.signature = new B4String(in);
	// ��������� ������������ ���������
	assert signature.getValue().equals("8BIM") : signature.getValue();

	this.blendModeKey = new B4String(in);
	this.opacity = in.readByte();
	this.clipping = in.readByte();
	this.flags = in.readByte();

	// zero
	in.readByte();

	int nextSize = in.readInt();
	int current = in.available();

	// ��� ��� ������ LayerMaskData (2-21) - ���������� ������ ����
	in.skipBytes(in.readInt());
	// � ��� ��� ������ LayerBlendingRangesData (2-22)
	in.skipBytes(in.readInt());
	// �������� ����
	this.name = new PString(in);

	// ���������� ����������� ����
	in.skip(in.available() - (current - nextSize));
    }

    public Rect getBounds() {
	return bounds;
    }

    public List<ChannelLengthInfo> getChannelsLengthInfo() {
	return channelsLengthInfo;
    }

    public B4String getSignature() {
	return signature;
    }

    public B4String getBlendModeKey() {
	return blendModeKey;
    }

    public byte getOpacity() {
	return opacity;
    }

    public byte getClipping() {
	return clipping;
    }

    public byte getFlags() {
	return flags;
    }

    public PString getName() {
	return name;
    }

    public short getNumberChannels() {
	return numberChannels;
    }

}
