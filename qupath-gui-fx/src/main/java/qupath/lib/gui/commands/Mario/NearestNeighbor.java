package qupath.lib.gui.commands.Mario;

import java.util.HashMap;

public class NearestNeighbor {

	
	int imgWidth;
	int imgHeight;
	private MapManager mapManager;
	private BoundingBox boundingBox;
	
	
	
	public NearestNeighbor(int imgWidth, int imgHeight, int[] argb, MapManager mm){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.mapManager = mm;
		this.boundingBox = new BoundingBox(imgWidth, imgHeight, argb, mm);
	}
	
	
	public void z(){
		boundingBox.createBoundingBox();
		System.out.println("Fläche BoundingBox: " +boundingBox.areaBoundingBox());
	}
	
	
	
	
	
	
	
}
