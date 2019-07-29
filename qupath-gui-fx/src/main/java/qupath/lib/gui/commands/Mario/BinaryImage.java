package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.Image;

public class BinaryImage implements Image {

	@Override
	public void convertImage(int width, int height,int[] rgb , int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int pix = rgb[pos] & 0xff;

				if (pix < threshold) {
					pix = 0x00000000;
				} else {
					pix = 0xffffffff;
				}
				rgb[pos] = (0xFF << 24) | (pix << 16) | (pix << 8) | pix;
			}
		}
		
	}

}
