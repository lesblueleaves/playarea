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
public class Rect {
    private int top;

    private int left;

    private int bottom;

    private int right;

    /**
     * @param in
     * @throws IOException
     */
    public Rect(DataInputStream in) throws IOException {
	top = in.readInt();
	left = in.readInt();
	bottom = in.readInt();
	right = in.readInt();

    }

    public Rect() {

    }

    public void setTop(int top) {
	this.top = top;
    }

    public void setLeft(int left) {
	this.left = left;
    }

    public void setBottom(int bottom) {
	this.bottom = bottom;
    }

    public void setRight(int right) {
	this.right = right;
    }

    public int getTop() {
	return top;
    }

    public int getLeft() {
	return left;
    }

    public int getBottom() {
	return bottom;
    }

    public int getRight() {
	return right;
    }

    @Override
    public String toString() {
	final String TAB = "    ";
	String retValue = "";
	retValue = "Rect ( " + super.toString() + TAB + "top = " + this.top + TAB + "left = " + this.left + TAB + "bottom = " + this.bottom + TAB +
		"right = " + this.right + TAB + " )";
	return retValue;
    }

}
