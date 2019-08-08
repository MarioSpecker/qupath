package qupath.lib.gui.commands.Mario;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BoundaryTracingAlgo {

	private int imgWidth;
	private int imgHeight;
	private int [] argb;
	private int[][] label;
	private int[] resizedArrayWithLabels;
	private int[] contourArray;
	private int resizedArrayWidth;
	private int resizedArrayHeight;
	private static final int BORDERSIZE = 4;
	private int[] resultContour;
	private HashMap<Integer,Integer> labelAreaMap;
	private HashMap<Integer,ResultPolygon> polyMap;
	
	
	public BoundaryTracingAlgo(int imgWidth, int imgHeight, int[]argb, int[][] label, HashMap<Integer,ResultPolygon> polyMap, HashMap<Integer,Integer> labelAreaMap, int[] resultContour){
		
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.argb = argb;
		this.setLabel(label);
		this.resultContour = resultContour;
		this.setLabelAreaMap(labelAreaMap);
		this.setPolyMap(polyMap);
		this.resizedArrayWithLabels = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		this.contourArray = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		this.resizedArrayWidth = imgWidth+BORDERSIZE;
		this.resizedArrayHeight = imgHeight+BORDERSIZE;
	}


	public void searchContourStart(){
		for(int y = 0;y<getResizedArrayHeight();y++){
			for(int x = 0;x<getResizedArrayWidth();x++){
				//System.out.print(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]);
				if(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]!=0){
				
					if(getPolyMap().containsKey(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x])){					
					}
					else{
						int id = getResizedArrayWithLabels()[y*getResizedArrayWidth()+x];
						ResultPolygon re = new ResultPolygon(id, 0);
						getPolyMap().put(id, re);
						createContour(x, y, id);
					}
					//countPixelofArea(x, y);
				}
			}
			//System.out.println();
		}
	}
	
	
		
	public void createContour(int x, int y, int id){
		System.out.println("Enter create Contour");
		int currentX = x;					
		int currentY =y;
		int latestAddedX = x;
		int latestAddedY = y;
		int direction =1;
		int countRotation = 0;
		do{
			switch(direction){		//north
			case 1: currentY-=1;
				break;				//east
			case 2: currentX+=1;
				break;
			case 3: currentY+=1;				
				break;
			case 4: currentX-=1;
				break;
			}
			int pixel = getResizedArrayWithLabels()[currentY*getResizedArrayWidth()+currentX];
			if(pixel!=0){
				if(currentX==latestAddedX&&currentY==latestAddedY){
				}else{
					int pixWhite = 0xffffffff;
					contourArray[currentY*getResizedArrayWidth()+currentX] = (0xFF << 24) | (pixWhite << 16) | (pixWhite << 8) | pixWhite;
					latestAddedX = currentX;
					latestAddedY = currentY;
					getPolyMap().get(id).addPoint(currentX, currentY);
					
				}
				direction-=1;
				countRotation=0;
				
			}else{
				direction+=1;
				countRotation+=1;
			}
			if(countRotation==4){
				direction-=2;
				countRotation=0;
			}
			if(direction==5)
				direction=1;
			if(direction==0)
				direction=4;
				
			
		}while(currentX!=x||currentY!=y);
		System.out.println("Das ist mapID: " + id + "   " + getPolyMap().get(id).npoints);
	}
	
	

	private void countPixelofArea(int x, int y){

		Iterator hmIterator = getPolyMap().entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();

			if(rPoly.contains(x, y)){

			}
		} 
	}
	
	//Hier werden die Pixel von jedem Label gezaehlt 
	private void countPixelFromEachLabel(){
		for(int y = 0;y<getImgHeight();y++){
			for(int x = 0;x<getImgWidth();x++){
				int pixValue= getLabel()[y][x];
				if(getLabelAreaMap().containsKey(pixValue)){
					Iterator hmIterator = getLabelAreaMap().entrySet().iterator(); 
					while (hmIterator.hasNext()) { 
						Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
						int id = (int)mapElement.getKey();
						if((int)mapElement.getKey()==pixValue){
							int w = (int)mapElement.getValue()+1;
						}
					}
				}else{
					getLabelAreaMap().put(pixValue, 1);
				}
			}
		}
	}
			

		//Hier werden die Werte von einer 2er Matrix (label) in einem vergrösserten(2 Pixel Rand an jeder seite) 1d Array gespeichert	
		public void labelToArray(){
			for(int y = 0;y<getImgHeight();y++){
				for(int x = 0;x<getImgWidth();x++){
					getResizedArrayWithLabels()[(y+2)*getResizedArrayWidth()+(x+2)] = getLabel()[y][x];
				}
			}
		}
		
		
		private void decreaseArray(){
			for(int y = 0;y<getImgHeight();y++){
				for(int x = 0;x<getImgWidth();x++){
					getResultContour()[y*getImgWidth()+x] =  getContourArray()[(y+2)*getImgWidth()+(x+2)];
				}
			}
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
		
		public int[] getContourArray() {
			return contourArray;
		}




		public void setContourArray(int[] contourArray) {
			this.contourArray = contourArray;
		}




		public int[] getResultContour() {
			return resultContour;
		}




		public void setResultContour(int[] resultContour) {
			this.resultContour = resultContour;
		}




		




	




		public int[] getResizedArrayWithLabels() {
			return resizedArrayWithLabels;
		}




		public void setResizedArrayWithLabels(int[] resizedArrayWithLabels) {
			this.resizedArrayWithLabels = resizedArrayWithLabels;
		}
		
		public int getResizedArrayWidth() {
			return resizedArrayWidth;
		}




		public void setResizedArrayWidth(int resizedArrayWidth) {
			this.resizedArrayWidth = resizedArrayWidth;
		}




		public int getResizedArrayHeight() {
			return resizedArrayHeight;
		}




		public void setResizedArrayHeight(int resizedArrayHeight) {
			this.resizedArrayHeight = resizedArrayHeight;
		}


		public HashMap<Integer,Integer> getLabelAreaMap() {
			return labelAreaMap;
		}


		public void setLabelAreaMap(HashMap<Integer,Integer> labelAreaMap) {
			this.labelAreaMap = labelAreaMap;
		}


		public HashMap<Integer,ResultPolygon> getPolyMap() {
			return polyMap;
		}


		public void setPolyMap(HashMap<Integer,ResultPolygon> polyMap) {
			this.polyMap = polyMap;
		}


		public int[][] getLabel() {
			return label;
		}


		public void setLabel(int[][] label) {
			this.label = label;
		}

}
