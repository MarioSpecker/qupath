package qupath.lib.gui.commands.Mario;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Contour {

	
	private int[][] label;
	private HashMap<Integer,Integer> labelAreaMap;
	private HashMap<Integer,ResultPolygon> polyMap;
	private int[] resultContour;
	private int imgWidth;
	private int imgHeight;
	
	
	public Contour(int imgWidth, int imgHeight, int[] argb){
		this.resultContour = new int[imgWidth*imgWidth];
		this.label= new int[imgHeight][imgWidth];
		BoundaryTracingAlgo bTA = new BoundaryTracingAlgo(imgWidth, imgHeight, argb, label, polyMap, labelAreaMap, resultContour);
		TwoPassAlgo twoPassAlgo = new TwoPassAlgo(imgWidth, imgHeight, argb, label);
		this.polyMap = new HashMap<>();
		this.labelAreaMap = new HashMap<>();
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
	}
	
	
	
	
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
	
	
	
	
	public int[][] getLabel() {
		return label;
	}

	public void setLabel(int[][] label) {
		this.label = label;
	}

	public HashMap<Integer, Integer> getLabelAreaMap() {
		return labelAreaMap;
	}

	public void setLabelAreaMap(HashMap<Integer, Integer> labelAreaMap) {
		this.labelAreaMap = labelAreaMap;
	}

	public HashMap<Integer, ResultPolygon> getPolyMap() {
		return polyMap;
	}

	public void setPolyMap(HashMap<Integer, ResultPolygon> polyMap) {
		this.polyMap = polyMap;
	}
	
	public int[] getResultContour() {
		return resultContour;
	}

	public void setResultContour(int[] resultContour) {
		this.resultContour = resultContour;
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
}
