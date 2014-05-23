/**
 * 
 */
package com.alternativagame.resource.utils.psd.layer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.IOException;

import com.alternativagame.resource.utils.psd.images.RAWParser;
import com.alternativagame.resource.utils.psd.images.RLEParser;
import com.alternativagame.resource.utils.psd.types.Rect;

/**
 * PSD-parser
 * http://blog.alternativaplatform.com/en/2007/07/09/parser-psd-formata/
 * @author Alexey Kviring
 */
public class PSDLayerPixelData {

    private BufferedImage image;

    /**
     * @param in
     * @throws IOException
     */
    public PSDLayerPixelData(PSDLayerStructure layer, DataInputStream in) throws IOException {
	Rect bounds = layer.getBounds();
	int heigth = bounds.getBottom() - bounds.getTop();
	int width = bounds.getRight() - bounds.getLeft();

	byte[][] channelsData = new byte[layer.getNumberChannels()][];
	// ���� �� �������
	for (int channel = 0; channel < layer.getNumberChannels(); channel++) {
	    short compressionMethod = in.readShort();
	    switch (compressionMethod) {
	    case 0:
		RAWParser raw = new RAWParser(width, heigth, in);
		channelsData[channel] = raw.getData();
		break;
	    case 1:
		RLEParser rle = new RLEParser(width, heigth, in);
		channelsData[channel] = rle.getData();
		break;

	    default:
		assert false;
		break;
	    }
	}

	// ���� ������ � ������ �� ����� ���� - ��������� �����������
	if ((width > 0) && (heigth > 0) && (layer.getNumberChannels() == 4)) {
	    // ������ �����������
	    this.image = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_ARGB);
	    WritableRaster raster = image.getRaster();
	    // rgb ������
	    int i = 0;
	    for (int y = 0; y < heigth; y++)
		for (int x = 0; x < width; x++) {
		    {
			// b r g a
			raster.setPixel(x, y, new int[] { channelsData[1][i], channelsData[2][i], channelsData[3][i], channelsData[0][i] });
			i++;
		    }
		}
	}
    }

    public BufferedImage getImage() {
	return image;
    }

}
