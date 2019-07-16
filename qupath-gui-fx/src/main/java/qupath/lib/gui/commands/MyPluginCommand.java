package qupath.lib.gui.commands;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Mario
 *
 */
public class MyPluginCommand implements PathCommand {

	private QuPathGUI qupath;
	private byte[] argb;

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
	}

	@Override
	public void run() {

		BufferedImage img = qupath.getViewer().getThumbnail();
		try {
			argb = toByteArrayAutoClosable(img, "png");
		} catch (IOException e) {

			e.printStackTrace();
		}
		toGrayScale(argb);
		int threshold = getIterativeThreshold(argb, img.getHeight(), img.getWidth());
		System.out.println(threshold);
		

	}

	private static byte[] toByteArrayAutoClosable(BufferedImage image,
			String type) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(image, type, out);
			return out.toByteArray();
		}
	}

//	@SuppressWarnings("unused")
//	private void binarize(byte[] bytes, int threshold) {
//		for (int pos = 0; pos < bytes.length; pos++) {
//
//			if (avg < threshold) {
//				avg = 0x00000000;
//			} else {
//				avg = 0xffffffff;
//			}
//
//			bytes[pos] = (byte) ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
//		}
//	}

	private void toGrayScale(byte[] bytes) {

		for (int i = 0; i < bytes.length; i++) {
			int r = (bytes[i] >> 16) & 0xff;
			int g = (bytes[i] >> 8) & 0xff;
			int b = bytes[i] & 0xff;
			int avg = (r + g + b) / 3;
			bytes[i] = (byte) ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
		}
	}

	private int getIterativeThreshold(byte[] argb, int width, int height) {
		long totalSmallerThreshold = 0;
		long totalTallerThreshold = 0;
		long countPixelSmaller = 0;
		long countPixelTaller = 0;
		int currentThreshold = 127;
		int newThreshold = 127;
		float averageSmallerThreshold = 0;
		float averageTallerThreshold = 0;

		do {
			currentThreshold = newThreshold;
			System.out.println("Das ist der Current Threshold1 " + currentThreshold);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					int pix = argb[pos];
					pix = (pix & 0x0000ff);
					System.out.println("+++++++" +pix);
					if (pix <= currentThreshold) {
						totalSmallerThreshold += pix;
						countPixelSmaller++;
					} else {
						totalTallerThreshold += pix;
						countPixelTaller++;
					}
				}
			}
			averageSmallerThreshold = totalSmallerThreshold / countPixelSmaller;
			averageTallerThreshold = totalTallerThreshold / countPixelTaller;
			float x = (averageSmallerThreshold + averageTallerThreshold) / 2;
			newThreshold = (int) x;
			System.out.println("Das ist der New Threshold" + newThreshold);
		} while ((newThreshold - currentThreshold) >= 1);
		return currentThreshold;
	}
}
