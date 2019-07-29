package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.Filter;

public class LaPlaceFilter implements Filter{

	@Override
	public void filterOperation(int width, int height, int sizeBorder, int gridSize, int[] argb, int[] updArray ) {
		for (int y = sizeBorder; y < height-sizeBorder; y++) {
			for (int x = sizeBorder; x<width -sizeBorder; x++) { 
				int pixel;
				if(sizeBorder==1)
					pixel = getPixelFromLP3(x, y, width, argb);
				else
					pixel = getPixelFromLP5(x, y, width, argb);	
				if(pixel<0)pixel=0;
				else if(pixel>255)pixel=255;
				updArray[y*width+x] = ((0xFF << 24) | (pixel << 16) | (pixel << 8) | pixel);
			}
		}
		
	}
	
	private int getPixelFromLP3(int x, int y, int width, int[] argb){
		int pix2 = argb[(y-1)*width+x]&0xff;
		int pix4 = argb[y*width+(x-1)]&0xff;
		int pix5 = argb[y*width+x]&0xff;
		int pix6 = argb[(y)*width+(x+1)]&0xff;
		int pix8 = argb[(y+1)*width+(x)]&0xff;
		int pixel = (-pix2 - pix4 + 4*pix5 -pix6 - pix8);
		return pixel;
	}
	
	//LaPlace Kantendetektion -> 5X5 Matrix
	private int getPixelFromLP5(int x, int y, int width, int[] argb){
		int pix02 = argb[(y-2)*width+x]&0xff;
		int pix11 = argb[(y-1)*width+(x-1)]&0xff;
		int pix12 = argb[(y-1)*width+x]&0xff;
		int pix13 = argb[(y-1)*width+(x+1)]&0xff;
		int pix20 = argb[y*width+(x-2)]&0xff;
		int pix21 = argb[y*width+(x-1)]&0xff;
		int pix22 = argb[y*width+x]&0xff;
		int pix23 = argb[y*width+(x+1)]&0xff;
		int pix24 = argb[y*width+(x+2)]&0xff;
		int pix31 = argb[(y+1)*width+(x-1)]&0xff;
		int pix32 = argb[(y+1)*width+x]&0xff;
		int pix33 = argb[(y+1)*width+(x+1)]&0xff;
		int pix42 = argb[(y+2)*width+x]&0xff;
		int pixel = (-pix02 - pix11 -(2*pix12)- pix13 -pix20 - (2*pix21) +(16*pix22) -(2*pix23)
				- pix24 - pix31 -(2+pix32) -pix33 - pix42);
		return pixel;
	}

}
