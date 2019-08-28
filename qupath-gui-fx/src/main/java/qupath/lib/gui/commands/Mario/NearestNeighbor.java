package qupath.lib.gui.commands.Mario;

public class NearestNeighbor {

	
	int imgWidth;
	int imgHeight;
	private BoundingBox boundingBox;
	
	
	public NearestNeighbor(int imgWidth, int imgHeight, int[] argb){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.boundingBox = new BoundingBox(imgWidth, imgHeight, argb);
	}
	
	
	public void z(){
		boundingBox.createBoundingBox();
		System.out.println("Fläche BoundingBox: " +boundingBox.areaBoundingBox());
	}
	
	
	
	
	
	
	
}
