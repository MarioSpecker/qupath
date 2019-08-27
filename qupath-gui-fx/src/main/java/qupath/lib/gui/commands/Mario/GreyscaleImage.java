package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.Image;

public class GreyscaleImage implements Image{

	@Override
	public void convertImage(int width, int height,int[] rgb , int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int r = (int)(((rgb[pos] >> 16) & 0xff) *(0.299));
				int g = (int)(((rgb[pos] >> 8) & 0xff) *(0.578));
				int b = (int)((rgb[pos] & 0xff)*(0.114));
				int avg = (r + g + b);
				rgb[pos] = ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
			}
		}
		
	}

}
