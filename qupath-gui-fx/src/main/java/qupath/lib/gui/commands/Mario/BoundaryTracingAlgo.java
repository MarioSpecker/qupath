package qupath.lib.gui.commands.Mario;

import java.awt.image.BufferedImage;

public class BoundaryTracingAlgo {

	private int width;
	


	private int height;
	private int [] argb;

	public BoundaryTracingAlgo(int width, int height, int[]argb){
		this.width = width;
		this.height = height;
		this.argb = argb;

	}


	public void pavelAlgo(){
		BufferedImage resizedImage = new BufferedImage(getWidth() +4 , getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
		int[] resizedArray = new int[resizedImage.getHeight() * resizedImage.getWidth()];
		for(int y = 0; y<getHeight(); y++){
			for(int x = 0; x<getWidth(); x++){
				int pos = y*width+x;
				int pix = getArgb()[pos]&0xff;
				
			}
		}
	}
	
	
	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int[] getArgb() {
		return argb;
	}


	public void setArgb(int[] argb) {
		this.argb = argb;
	}


}
