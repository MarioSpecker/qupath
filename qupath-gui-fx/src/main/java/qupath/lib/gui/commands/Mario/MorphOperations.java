package qupath.lib.gui.commands.Mario;

import java.awt.image.BufferedImage;

public interface MorphOperations {

	public void prepareMorphOperation(BufferedImage resizedImage, int[] resizedArray, int[] argbArray, int widthDefaultImage, int heightDefaultImage);
	
	public void executeMorpOperation(int width, int height, int[] resizedArray, int[] rgb, int defaultImageWidth, int halfKernelSize, boolean [][]kernel);
}
