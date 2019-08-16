package qupath.lib.gui.commands.Mario;

public class HelperFunctions {

	
	public HelperFunctions(){
		
	}
	
	
	public void invertImage(int width, int height, int[] argb){
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int pos = y*width+x;
				if((argb[pos]&0xff)==255)
					argb[pos] = 0x00000000; 
				else
					argb[pos] = 0xffffffff;
			}
		}
	}
}
