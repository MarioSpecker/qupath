package qupath.lib.gui.commands.Mario;

import java.awt.Polygon;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import qupath.lib.gui.commands.Mario.ResultPolygon;
import qupath.lib.roi.PolygonROI;


public class TwoPassAlgo {

	
	private int[][] label;
	private int[] argb;
	private int imgWidth;
	private int imgHeight;
	List<Set<Integer>> allSets; 
	private int labelCounter;
	private int neighbour;
	private int matrix[][];
	private HashMap<Integer,ResultPolygon> polyMap;
	private int[] resizedArrayWithLabels;
	private int[] contourArray;
	private int resizedArrayWidth;
	private int resizedArrayHeight;
	private static final int BORDERSIZE = 4;
	private int currentX;  
	private int currentY ;
	private int direction;
	private int[] resultContour;
	private HashMap<Integer,Integer> labelAreaMap;



	public TwoPassAlgo(int imgWidth, int imgHeight, int[]argb){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		label = new int[imgHeight][imgWidth];
		this.argb = argb;
		allSets = new ArrayList<Set<Integer>>();
		labelCounter = 0; 
		neighbour = 0;
		matrix = new int[imgHeight][imgWidth];
		resizedArrayWithLabels = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		contourArray = new int[(imgHeight+BORDERSIZE)*(imgWidth+BORDERSIZE)];
		resizedArrayWidth = imgWidth+BORDERSIZE;
		resizedArrayHeight = imgHeight+BORDERSIZE;
		direction = 1;
		resultContour = new int[imgWidth*imgWidth];
		this.polyMap = new HashMap<>();
		this.labelAreaMap = new HashMap<>();
		
	}
	
	
	
	
	public int getDirection() {
		return direction;
	}




	public void setDirection(int direction) {
		this.direction = direction;
	}




	public void createFirstStep(){
		for (int x = 0; x < getImgHeight(); x++) {
            for (int y = 0; y < getImgWidth(); y++) {
                getMatrix()[x][y] = getArgb()[x*getImgWidth()+y]&0xFF;
           }
        }
		
        for (int x = 0; x < getImgHeight(); x++) {
            for (int y = 0; y < getImgWidth(); y++) {
                if ((getMatrix()[x][y]) != 0) {
                    Set<Integer> neighbours = new HashSet<>();
                    checkNeighbours(x, y, getImgWidth(), neighbours);
                    if (neighbours.isEmpty()) {
                        Set<Integer> set = new HashSet<>();
                        set.add(getLabelCounter());
                        getAllSets().add(set);
                        getLabel()[x][y] = getLabelCounter();
                        setLabelCounter(getLabelCounter() + 1);
                    } else if (neighbours.size() == 1) {
                    	getLabel()[x][y] = neighbours.iterator().next();
                    } else if (neighbours.size() > 1) {
                        int pixValue = Collections.min(neighbours);
                        getLabel()[x][y] = pixValue;
                        for (int neighbor : neighbours) {
                            getAllSets().get(neighbor).addAll(neighbours);
                        }
                    }
                }
            }
        }
 	}
	
	
	public void createSecondStep(){
		for (int x = 0; x < getImgHeight(); x++) {
			for (int y = 0; y < getImgWidth(); y++) {
				Set<Integer> ar = getAllSets().get(getLabel()[x][y]);
				for(int l : ar){
					if(l<getLabel()[x][y])
						getLabel()[x][y] = l;
				}

			}
		}
	}
	

	
	private void checkNeighbours(int x, int y, int width, Set<Integer> neighbours) {
        for (int i = -1; i < 1; i++) {
            for (int j = -1; j < 2; j++) {

                if (x + i < 0 || y + j < 0 || y + j >= width) {
                } else {
                    if ((i == 0 && j == 0) || (i == 0 && j == 1)) {

                    } else {
                        if (getLabel()[x + i][y + j] != 0) {
                            neighbours.add(getLabel()[x + i][y + j]);
                        }
                    }
                }
            }
        }
    }
	
	
	
	public void searchContourStart(){
		for(int y = 0;y<getResizedArrayHeight();y++){
			for(int x = 0;x<getResizedArrayWidth();x++){
				//System.out.print(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]);
				if(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]!=0){
				
					if(polyMap.containsKey(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x])){					
					}
					else{
						int id = getResizedArrayWithLabels()[y*getResizedArrayWidth()+x];
						ResultPolygon re = new ResultPolygon(id, 0);
						polyMap.put(id, re);
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
		System.out.println("Das ist mapID: " + id + "   " + polyMap.get(id).npoints);
	}
	
	

	private void countPixelofArea(int x, int y){
		
		Iterator hmIterator = polyMap.entrySet().iterator(); 
		  
        while (hmIterator.hasNext()) { 
            Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            int id = (int)mapElement.getKey();
            ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
            
            if(rPoly.contains(x, y)){
            	
            }
            
            //System.out.println(mapElement.getKey() + " : " + marks);
            
        } 
        
	}
	
	
	//Hier werden die Pixel von jedem Label gezaehlt
	private void countPixelFromEachLabel(){
		for(int y = 0;y<getImgHeight();y++){
			for(int x = 0;x<getImgWidth();x++){
				int pixValue= label[y][x];
				if(labelAreaMap.containsKey(pixValue)){
					Iterator hmIterator = labelAreaMap.entrySet().iterator(); 
					while (hmIterator.hasNext()) { 
						Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
						int id = (int)mapElement.getKey();
						if((int)mapElement.getKey()==pixValue){
							int w = (int)mapElement.getValue()+1;
						}
					}
				}else{
					labelAreaMap.put(pixValue, 1);
				}
			}
		}
	}
		

		
	
	

	
	
	
	
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
	
	
	public void pavlidisAlgo(){
		labelToArray();
		searchContourStart();
		decreaseArray();
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




	public HashMap<Integer, ResultPolygon> getPolyMap() {
		return polyMap;
	}




	public void setPolyMap(HashMap<Integer, ResultPolygon> polyMap) {
		this.polyMap = polyMap;
	}




	public int[] getResizedArrayWithLabels() {
		return resizedArrayWithLabels;
	}




	public void setResizedArrayWithLabels(int[] resizedArrayWithLabels) {
		this.resizedArrayWithLabels = resizedArrayWithLabels;
	}




	public int[][] getLabel() {
		return label;
	}






	public void setLabel(int[][] label) {
		this.label = label;
	}






	public List<Set<Integer>> getAllSets() {
		return allSets;
	}






	public void setAllSets(List<Set<Integer>> allSets) {
		this.allSets = allSets;
	}






	public int getNeighbour() {
		return neighbour;
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


	public void setNeighbour(int neighbour) {
		this.neighbour = neighbour;
	}






	public int[][] getMatrix() {
		return matrix;
	}






	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}






	public int[] getArgb() {
		return argb;
	}
	
	public int getCurrentX() {
		return currentX;
	}




	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}




	public int getCurrentY() {
		return currentY;
	}




	public void setCurrentY(int currentY) {
		this.currentY = currentY;
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
