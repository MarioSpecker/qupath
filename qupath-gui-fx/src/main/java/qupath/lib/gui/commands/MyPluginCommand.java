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

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
	}

	@Override
	public void run() {

		BufferedImage img = qupath.getViewer().getThumbnail();
		try {
			byte[] bytes = toByteArrayAutoClosable(img, "png");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	

	private static byte[] toByteArrayAutoClosable(BufferedImage image,
			String type) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(image, type, out);
			return out.toByteArray();
		}
	}

	@SuppressWarnings("unused")
	private void binarize(byte[] bytes, int threshold) {
		for (int pos = 0; pos < bytes.length; pos++) {

			// so that it also works with color picture
			int r = (bytes[pos] >> 16) & 0xff;
			int g = (bytes[pos] >> 8) & 0xff;
			int b = bytes[pos] & 0xff;
			int grayScale = (r + g + b) / 3;

			if (grayScale < threshold) {
				grayScale = 0x00000000;
			} else {
				grayScale = 0xffffffff;
			}

			bytes[pos] = (byte) ((0xFF << 24) | (grayScale << 16)
					| (grayScale << 8) | grayScale);
		}
	}
}
