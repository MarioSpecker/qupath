package qupath.lib.gui.commands.Mario;

import java.util.HashMap;

public class NearestNeighbor {

	
	int imgWidth;
	int imgHeight;
	private BoundingBox boundingBox;
	private HashMap<Integer,Integer> labelAreaMap;			//Id jeden Objekts mit Flächeninhalt Pixel
	private HashMap<Integer,ResultPolygon> polyMap;
	
	
	public NearestNeighbor(int imgWidth, int imgHeight, int[] argb, HashMap<Integer,ResultPolygon> polyMap,HashMap<Integer,Integer> labelAreaMap){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.boundingBox = new BoundingBox(imgWidth, imgHeight, argb, polyMap, labelAreaMap);
	}
	
	
	public void z(){
		boundingBox.createBoundingBox();
		System.out.println("Fläche BoundingBox: " +boundingBox.areaBoundingBox());
	}
	
	
	
	
	
	
	
}
