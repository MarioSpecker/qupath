package qupath.lib.gui.commands.Mario;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
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
	
	



	public TwoPassAlgo(int imgWidth, int imgHeight, int[]argb, int[][] label){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.argb = argb;
		allSets = new ArrayList<Set<Integer>>();
		labelCounter = 0; 
		neighbour = 0;
		matrix = new int[imgHeight][imgWidth];
		this.label = label;
		
		
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
					}
					else {
						if (getLabel()[x + i][y + j] != 0) {
							neighbours.add(getLabel()[x + i][y + j]);
						}
					}
				}
			}
		}
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
