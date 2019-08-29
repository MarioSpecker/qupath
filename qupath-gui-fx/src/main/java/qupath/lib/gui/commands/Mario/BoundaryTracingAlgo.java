package qupath.lib.gui.commands.Mario;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.awt.Polygon;

import qupath.lib.gui.commands.Mario.ResultPolygon;

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
	private MapManager mapManager;
	Vector<Polygon> polyvec;
	Vector<ResultPolygon> polygonResultVector;
	private int [] argbCopy;
	private int polyID;
	
	
	public BoundaryTracingAlgo(int imgWidth, int imgHeight, int[]argb, int[][] label, int[] resultContour, MapManager mm){
		
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.argb = argb;
		this.setLabel(label);
		this.resultContour = resultContour;
		this.resizedArrayWithLabels = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		this.contourArray = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		this.resizedArrayWidth = imgWidth+BORDERSIZE;
		this.resizedArrayHeight = imgHeight+BORDERSIZE;
		this.mapManager = mm;
		argbCopy = new int[(imgWidth+BORDERSIZE)*(imgHeight+BORDERSIZE)] ;
		this.polyID = 1;
	}


	public void searchContourStart(HashMap<Integer,ResultPolygon> polyMap){
		for(int y = 0;y<getResizedArrayHeight();y++){
			for(int x = 0;x<getResizedArrayWidth();x++){
				//System.out.print(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]);
				int pixel = argbCopy[y*getResizedArrayWidth()+x] & 0xff;
				if(pixel==0){
					
					if(countPixelofArea(x, y, mapManager.getPolyMap())){
						//System.out.println("Betritt Count Pixel.....");
					}
					else{
						ResultPolygon re = new ResultPolygon(polyID, 0);
						polyMap.put(polyID, re);
						createContour(x, y, polyID, polyMap);
						polyID++;
					}
					countPixelofArea(x, y, polyMap);
				}
			}
		}
	}
	
	
	public void createContour(int x, int y, int id, HashMap<Integer,ResultPolygon> polyMap){
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
			int pixel = argbCopy[currentY*getResizedArrayWidth()+currentX]& 0xff;
			if(pixel==0){
				if(currentX==latestAddedX&&currentY==latestAddedY){
				}else{
					int pixWhite = 0xffffffff;
					contourArray[currentY*getResizedArrayWidth()+currentX] = (0xFF << 24) | (pixWhite << 16) | (pixWhite << 8) | pixWhite;
					latestAddedX = currentX;
					latestAddedY = currentY;
					polyMap.get(id).addPoint(currentX, currentY);
					
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
		System.out.println("Verlässt kontur");
	}
	
	

	private boolean countPixelofArea(int x, int y, HashMap<Integer,ResultPolygon> polyMap){

		Iterator hmIterator = polyMap.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			if(rPoly.contains(x, y)){
				//System.out.println("Betritt while");
				rPoly.setSizeOfPixel(rPoly.getSizeOfPixel()+1);
				return true;
			}
			for(int i=0; i<rPoly.npoints;i++){
				if((rPoly.xpoints[i]==x)&&(rPoly.ypoints[i]==y)){
					rPoly.setSizeOfPixel(rPoly.getSizeOfPixel()+1);
					return true;
				}
			}
		} 
		return false;
	}


	//Hier werden die Werte von einer 2er Matrix (label) in einem vergrösserten(2 Pixel Rand an jeder seite) 1d Array gespeichert	
	public void labelToArray(){
		for(int y = 0;y<getImgHeight();y++){
			for(int x = 0;x<getImgWidth();x++){
				getResizedArrayWithLabels()[(y+2)*getResizedArrayWidth()+(x+2)] = getLabel()[y][x];
			}
		}
	}
	
	


	public void decreaseArray(){
		for(int y = 0;y<getImgHeight();y++){
			for(int x = 0;x<getImgWidth();x++){
				getResultContour()[y*getImgWidth()+x] =  getContourArray()[(y+2)*getResizedArrayWidth()+(x+2)];
				//getResultContour()[y*getImgWidth()+x] = [y*getImgWidth()+x]
			}
		}
	}
	
	public void fillArgbResizedArray(){
		int pixel = 255;
		for(int y=0;y<getResizedArrayHeight();y++){
			for(int x=0;x<getResizedArrayWidth();x++){
				argbCopy[y*getResizedArrayWidth()+x] = ((0xFF << 24) | (pixel << 16) | (pixel << 8) | pixel);
			}
		}
		for(int y=0;y<imgHeight;y++){
			for(int x=0;x<imgWidth;x++){
				argbCopy[(y+2)*(getResizedArrayWidth())+(x+2)] = argb[y*imgWidth+x];
			}
		}
		for(int y=0;y<getResizedArrayHeight();y++){
			for(int x=0;x<getResizedArrayWidth();x++){
				int pi = argbCopy[y*getResizedArrayWidth()+x]&0xff;
				System.out.print(pi);
			}
			System.out.println("");
		}
	}
		
		
		
		//***************************************Getter / Setter******************************************************
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

		public int[][] getLabel() {
			return label;
		}

		public void setLabel(int[][] label) {
			this.label = label;
		}

}
