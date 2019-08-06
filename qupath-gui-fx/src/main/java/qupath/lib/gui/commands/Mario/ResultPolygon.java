package qupath.lib.gui.commands.Mario;

import java.awt.Polygon;

public class ResultPolygon extends Polygon{
	
	int id;
	int sizeOfPixel;
	
	public ResultPolygon(int id, int sizeOfPixel){
		super();
		this.id = id;
		this.sizeOfPixel = sizeOfPixel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSizeOfPixel() {
		return sizeOfPixel;
	}

	public void setSizeOfPixel(int sizeOfPixel) {
		this.sizeOfPixel = sizeOfPixel;
	}
	
	
	
	

}
