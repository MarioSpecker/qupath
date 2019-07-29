package qupath.lib.gui.commands.Mario;
import qupath.lib.gui.commands.Mario.interfa.Filter;

public class GaussFilter implements Filter {

	@Override
	public void filterOperation(int width, int height, int sizeBorder, int gridSize, int[] argb, int[] updArray) {
		int pixel;
		for (int y = sizeBorder; y < height-sizeBorder; y++) {
			for (int x = sizeBorder; x<width -sizeBorder; x++) {
				pixel = getPixelFromGauss5x5Matrix(x, y, width, gridSize, sizeBorder,  argb) ;
				updArray[y*width+x] = ((0xFF << 24) | (pixel << 16) | (pixel << 8) | pixel);
			}
		}
		
	}
	
	private int getPixelFromGauss5x5Matrix(int x, int y, int width, int gridSize, int sizeBorder, int[] argb){
		int [][] a= new int[gridSize][gridSize];
		for (int j = -sizeBorder; j < sizeBorder; j++) {
			for (int i = -sizeBorder; i<sizeBorder; i++) {
				a[i+2][j+2] = argb[(y-j)*width+(x-i)]&0x000000FF;	
			}
		}
		double pixel = (1d/273*a[0][0])+(4d/273*a[0][1])+(7d/273*a[0][2])+(4d/273*a[0][3])+(1d/273*a[0][4])
				+(4d/273*a[1][0])+(16d/273*a[1][1])+(26d/273*a[1][2])+(16d/273*a[1][3])+(4d/273*a[1][4])
				+(7d/273*a[2][0])+(26d/273*a[2][1])+(41d/273*a[2][2])+(26d/273*a[2][3])+(7d/273*a[2][4])
				+(4d/273*a[3][0])+(16d/273*a[3][1])+(26d/273*a[3][2])+(16d/273*a[3][3])+(4d/273*a[3][4])
				+(1d/273*a[4][0])+(4d/273*a[4][1])+(7d/273*a[4][2])+(4d/273*a[4][3])+(1d/273*a[4][4]);
		int pix = (int)pixel;
		if(pix<0)pix=0;
		else if(pix>255)pix=255;
		return pix;
	}

}
