package qupath.lib.gui.commands.Mario;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
public class TwoPassAlgo {

	
	private int[] startArray;
	private int[] argb;
	private int imgWidth;
	private int imgHeight;
	List<Set<Integer>> allSets; 
	private int labelCounter;
	private int neighbour;
	
	public TwoPassAlgo(int imgWidth, int imgHeight, int[]argb){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		//label = new int[imgWidth][imgHeight];
		startArray = new int[imgWidth * imgHeight];
		this.argb = argb;
		allSets = new ArrayList<Set<Integer>>();
		labelCounter = 1; 
		neighbour = 0;
		
	}
	
	
	
	private void createStartArray(){
		for(int y=0;y<getImgHeight();y++){
			for(int x=0;x<getImgWidth();x++){
				int pos = y*getImgWidth()+x;
				if((getArgb()[pos]&0xff)==0){
					getStartArray()[pos] = 0;
				}
				else{
					getStartArray()[pos] = 1;
				}
			}
		}
	}
	
	
	private void createFirstStepArray(int width, int height, int[] startArray){
		for(int y=0;y<getImgHeight();y++){
			for(int x=0;x<getImgWidth();x++){
				int pos = y*width+x;
				
				if((getArgb()[pos]&0xff)!=0){
					neighbour = checkNeighbour(x, y, width);
					if(neighbour==0){
						Set<Integer> a = new HashSet<Integer>();
						a.add(getLabelCounter());
	                    allSets.add(a);
	                    getStartArray()[pos] = getLabelCounter();
						setLabelCounter(getLabelCounter()+1);
					}
					else if(neighbour==1)
						getStartArray()[pos] = getLabelCounter();
					else{
					
					}
						
					
				}
			}
		}
	}
	
	
	private int checkNeighbour(int x, int y, int width){
		int counter =0;
		int pix00, pix01, pix02, pix10;
		int pos00 = (y-1)*width+(x-1);
		int pos01 = (y-1)*width+(x-1);
		int pos02 = (y-1)*width+(x-1);
		int pos10 = (y-1)*width+(x-1);
		if(pos00<=0){
			pix00 = getArgb()[pos00]&0xff;
		}
		else if(pos01<=0){
			pix01 = getArgb()[pos00]&0xff;
		}
		else if(pos02<=0){
			pix02 = getArgb()[(y-1)*width+(x+1)]&0xff;
		}
		else if(pos10<=0){
			pix10 = getArgb()[(y)*width+(x-1)]&0xff;
		}
		return counter;
	}
	
	
	private void checkNeighbourNotBackground(int pix, int counter){
		if(pix==0){
			
		}
		else
			counter++;
	}
	



	



	public int[] getStartArray() {
		return startArray;
	}



	public void setStartArray(int[] startArray) {
		this.startArray = startArray;
	}



	public int[] getArgb() {
		return argb;
	}



	public void setArgb(int[] argb) {
		this.argb = argb;
	}



	public int getImgWidth() {
		return imgWidth;
	}



	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}



	public int getImgHeight() {
		return imgHeight;
	}



	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}



	public int getLabelCounter() {
		return labelCounter;
	}



	public void setLabelCounter(int labelCounter) {
		this.labelCounter = labelCounter;
	}
	
}
