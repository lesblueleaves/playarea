/**
 * 
 */
package com.alternativagame.resource.utils.psd.irb;

import java.io.DataInputStream;
import java.io.IOException;

import com.alternativagame.resource.utils.psd.types.Rect;
import com.alternativagame.resource.utils.psd.types.SliceRect;
import com.alternativagame.resource.utils.psd.types.VString;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class Slice {

    // �������������
    private final int id;

    // ������������� ������
    private final int groupID;

    // �� �������
    private final int origin;

    // ���
    private final VString name;

    // ���
    private final int type;

    // ����������
    private final Rect rect;

    // url
    private final VString url;

    // target
    private final VString target;

    // message
    private final VString message;

    // AllTag
    private final VString allTag;

    // cell is html
    private final boolean cellIsHtml;

    // cellText
    private final VString cellText;

    private final int hAlign;

    private final int vAlign;

    private final byte alphaColor;

    private final byte rColor;

    private final byte bColor;

    private final byte gColor;

    /**
     * @param in
     * @throws IOException
     */
    public Slice(DataInputStream in) throws IOException {
	id = in.readInt();
	groupID = in.readInt();
	origin = in.readInt();
	name = new VString(in);
	type = in.readInt();

	rect = new SliceRect(in);
	url = new VString(in);
	target = new VString(in);
	message = new VString(in);
	allTag = new VString(in);
	cellIsHtml = in.readBoolean();
	cellText = new VString(in);
	hAlign = in.readInt();
	vAlign = in.readInt();
	alphaColor = in.readByte();
	rColor = in.readByte();
	bColor = in.readByte();
	gColor = in.readByte();

    }

    @Override
    public String toString() {
	final String TAB = "    ";

	String retValue = "";

	retValue = "Slice ( " + TAB + "id = " + this.id + TAB + "groupID = " + this.groupID + TAB + "origin = " + this.origin + TAB + "name = " +
		this.name + TAB + "type = " + this.type + TAB + "rect = " + this.rect + TAB + "url = " + this.url + TAB + "target = " + this.target +
		TAB + "message = " + this.message + TAB + "allTag = " + this.allTag + TAB + "cellIsHtml = " + this.cellIsHtml + TAB + "cellText = " +
		this.cellText + TAB + "hAlign = " + this.hAlign + TAB + "vAlign = " + this.vAlign + TAB + "alphaColor = " + this.alphaColor + TAB +
		"rColor = " + this.rColor + TAB + "bColor = " + this.bColor + TAB + "gColor = " + this.gColor + TAB + " )";

	return retValue;
    }

    public int getId() {
	return id;
    }

    public int getGroupID() {
	return groupID;
    }

    public int getOrigin() {
	return origin;
    }

    public VString getName() {
	return name;
    }

    public int getType() {
	return type;
    }

    public Rect getRect() {
	return rect;
    }

    public VString getUrl() {
	return url;
    }

    public VString getTarget() {
	return target;
    }

    public VString getMessage() {
	return message;
    }

    public VString getAllTag() {
	return allTag;
    }

    public boolean isCellIsHtml() {
	return cellIsHtml;
    }

    public VString getCellText() {
	return cellText;
    }

    public int getHAlign() {
	return hAlign;
    }

    public int getVAlign() {
	return vAlign;
    }

    public byte getAlphaColor() {
	return alphaColor;
    }

    public byte getRColor() {
	return rColor;
    }

    public byte getBColor() {
	return bColor;
    }

    public byte getGColor() {
	return gColor;
    }

}
