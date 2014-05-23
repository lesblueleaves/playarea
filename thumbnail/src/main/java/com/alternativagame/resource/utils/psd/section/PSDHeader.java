/**
 * 
 */
package com.alternativagame.resource.utils.psd.section;

import java.io.DataInputStream;
import java.io.IOException;

import com.alternativagame.resource.utils.psd.types.B4String;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class PSDHeader {
    private final B4String signature;

    private final int version;

    private final int channel;

    private final int rows;

    private final int columns;

    private final int depth;

    private final int mode;

    public PSDHeader(DataInputStream in) throws IOException {
	// ������ ���������
	signature = new B4String(in);
	// ������
	version = in.readUnsignedShort();
	// 6 ���� �������
	in.skipBytes(6);
	// ���������� �������
	channel = in.readUnsignedShort();
	// �������
	rows = in.readInt();
	columns = in.readInt();
	// ������� �����
	depth = in.readUnsignedShort();
	// �����
	mode = in.readUnsignedShort();
    }

    public B4String getSignature() {
	return signature;
    }

    public int getVersion() {
	return version;
    }

    public int getChannel() {
	return channel;
    }

    public int getRows() {
	return rows;
    }

    public int getColumns() {
	return columns;
    }

    public int getDepth() {
	return depth;
    }

    public int getMode() {
	return mode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Signature:").append(signature).append('\n');
	builder.append("Version:").append(version).append('\n');
	builder.append("Channel:").append(channel).append('\n');
	builder.append("Rows:").append(rows).append('\n');
	builder.append("Columns:").append(columns).append('\n');
	builder.append("Depth:").append(depth).append('\n');
	builder.append("Mode:").append(mode).append('\n');
	return builder.toString();
    }
}
