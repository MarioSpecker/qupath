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
	
	
	
	private void searchContourStart(){
		for(int y = 0;y<getResizedArrayHeight();y++){
			for(int x = 0;x<getResizedArrayWidth();x++){
				if(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]!=0){
					if(polyMap.containsKey(getResizedArrayWithLabels()[y*getResizedArrayWidth()+x]));
					else{
						int id = getResizedArrayWithLabels()[y*getResizedArrayWidth()+x];
						createContour(x, y, id);
					}
				}
			}
		}
	}
	
	

	//direction clockwise -> 0 is North......
	

	
	
		
	private void createContour(int x, int y, int id){
		int currentX  = x;
		int currentY = y;
		int latestAddedX = x;
		int lattestAddedY = y;
		int direction = 1;
		int turnClockwise = 0;
		ResultPolygon rpoly = new ResultPolygon(id, 0);
		do{
			int pixel = getResizedArrayWithLabels()[currentY*getResizedArrayWidth()+currentX];
			if(pixel!=0){
				
				else{
					
				}
				
			


		}while((x!=currentX)&&(y!=currentY));
	}
		
		
		private void directionNorth(int currentX, int currentY, int latestAddedX, int latestAddedY){
			if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX-1)]!=0){
				setCurrentX(currentX-=1);
				setCurrentY(currentY-=1);
			}
			else if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX)]!=0){
				setCurrentY(currentY-=1);
			}
			else if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX+=1);
				setCurrentY(currentY-=1);
			}else{}
		}
		
		private void directionEast(int currentX, int currentY, int latestAddedX, int latestAddedY){
			if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX+=1);
				setCurrentY(currentY-=1);
			}
			else if(getResizedArrayWithLabels()[(currentY)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX+=1);
			}
			else if(getResizedArrayWithLabels()[(currentY+1)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX+=1);
				setCurrentY(currentY+=1);
			}else{}
		}
		
		private void directionSouth(int currentX, int currentY, int latestAddedX, int latestAddedY){
			if(getResizedArrayWithLabels()[(currentY+1)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX+=1);
				setCurrentY(currentY+=1);
			}
			else if(getResizedArrayWithLabels()[(currentY+1)*getResizedArrayWidth()+(currentX)]!=0){
				setCurrentY(currentY+=1);
			}
			else if(getResizedArrayWithLabels()[(currentY+1)*getResizedArrayWidth()+(currentX-1)]!=0){
				setCurrentX(currentX-=1);
				setCurrentY(currentY+=1);
			}else{}
		}
		
		private void directionWest(int currentX, int currentY, int latestAddedX, int latestAddedY){
			if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX-1)]!=0){
				setCurrentX(currentX-=1);
				setCurrentY(currentY+=1);
			}
			else if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX)]!=0){
				setCurrentX(currentX-=1);
			}
			else if(getResizedArrayWithLabels()[(currentY-1)*getResizedArrayWidth()+(currentX+1)]!=0){
				setCurrentX(currentX-=1);
				setCurrentY(currentY-=1);
			}else{}
		}
	
	
	
	private void labelToArray(int width, int height){
		for(int y = 0;y<height;y++){
			for(int x = 0;x<width;x++){
				getResizedArrayWithLabels()[(y+1)*width+(x+1)] = getLabel()[y][x];
			}
		}
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
