package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.MorphOperations;
import java.awt.image.BufferedImage;


public class Dilatation implements MorphOperations{

	@Override
	public void prepareMorphOperation(BufferedImage resizedImage, int[] resizedArray, int[] argbArray, int widthDefaultImage, int heightDefaultImage) {
		resizedImage.createGraphics().setColor(java.awt.Color.WHITE);
		resizedImage.createGraphics().fillRect(0, 0, resizedImage.getWidth(), resizedImage.getHeight());
		resizedImage.setRGB(2, 2, widthDefaultImage, heightDefaultImage, argbArray, 0, widthDefaultImage);
		resizedImage.getRGB(0, 0, resizedImage.getWidth(), resizedImage.getHeight(), resizedArray, 0, resizedImage.getWidth());
		
	}

	

	@Override
	public void executeMorpOperation(int width, int height, int[] resizedArray, int[] rgb, int defaultImageWidth, int halfKernelSize, boolean [][]kernel) {
		for (int y = 2; y < height - 2; y++) {
			for (int x = 2; x < width - 2; x++) {
				int pos = y * width + x;
				int pixelCenter = resizedArray[pos] & 0xff;
				if (pixelCenter == 255) {
					for (int j = -halfKernelSize; j <= halfKernelSize; j++) {
						for (int i = -halfKernelSize; i <= halfKernelSize; i++) {
							if (kernel[i + halfKernelSize][j + halfKernelSize] == true) {
								int pix = resizedArray[(y - j) * width+ (x - i)] & 0xff;
								if (pix == 0) {
									int black = 0x000000;
									int position = (y - halfKernelSize) * (defaultImageWidth) + (x -halfKernelSize);
									rgb[position] = (0xFF << 24)
											| (black << 16)
											| (black << 8)
											| black;
									break;
								}
							}
						}
					}
				}
			}
		}
		
	}

	
}
