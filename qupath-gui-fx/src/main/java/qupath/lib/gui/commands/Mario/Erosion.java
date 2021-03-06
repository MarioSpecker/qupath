package qupath.lib.gui.commands.Mario;
import java.awt.image.BufferedImage;
import qupath.lib.gui.commands.Mario.interfa.MorphOperations;

public class Erosion implements MorphOperations {

	
	

	@Override
	public void prepareMorphOperation(BufferedImage resizedImage, int[] resizedArray, int[] argbArray, int widthDefaultImage, int heightDefaultImage) {
		resizedImage.createGraphics().setColor(java.awt.Color.BLACK);
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
				if (pixelCenter == 0) {
					for (int j = -halfKernelSize; j <= halfKernelSize; j++) {
						for (int i = -halfKernelSize; i <= halfKernelSize; i++) {
							if (kernel[i + halfKernelSize][j + halfKernelSize] == true) {
								int pix = resizedArray[(y - j) * width+ (x - i)] & 0xff;
								if(pix == 255){
									int white = 0xffffff;
									rgb[(y - halfKernelSize) * (defaultImageWidth) + (x - halfKernelSize)] = (0xFF << 24)
											| (white << 16)
											| (white << 8)
											| white;
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
