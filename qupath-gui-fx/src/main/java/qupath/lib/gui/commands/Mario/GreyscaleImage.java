package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.Image;

public class GreyscaleImage implements Image{

	@Override
	public void convertImage(int width, int height,int[] rgb , int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int r = (rgb[pos] >> 16) & 0xff;
				int g = (rgb[pos] >> 8) & 0xff;
				int b = rgb[pos] & 0xff;
				int avg = (r + g + b) / 3;
				rgb[pos] = ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
			}
		}
		
	}

}
