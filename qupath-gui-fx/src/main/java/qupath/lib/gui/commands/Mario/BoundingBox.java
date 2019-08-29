package qupath.lib.gui.commands.Mario;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BoundingBox {
	
	
	private int imgWidth;
	private int imgHeight;
	private int xMaxPoint, yMaxPoint, xMinPoint, yMinPoint;
	private int lengthBoundingBox;
	private int widthBoundingBox;
	int []argb;
	private MapManager mapManager;
	
	
	
	public BoundingBox(int imgWidth, int imgHeight, int []argb, MapManager mm ){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.argb = argb;
		this.mapManager = mm;
	}
	
	
	
	//Hier wird die Länge und Breite der Bounding Box berechnet
	public void createBoundingBox(){
		int id = getIdOfLargestPolygon();
		ResultPolygon rPoly = mapManager.getPolyMap().get(id);
		getMinValue(rPoly);
		getMaxValue(rPoly);
		setLengthBoundingBox(getYMaxPoint() - getYMinPoint());
		setWidthBoundingBox(getXMaxPoint() - getXMinPoint());
	}
	
	//Grösste Polygon wird gesucht -> Sollte das Object sein   (Können mehrere Polygone sein da evtl nicht alle Flächen des Hintergrundes eliminiert wurden)
	private int getIdOfLargestPolygon(){
		int area = 0;
		int polyID = 0;
		Iterator hmIterator = mapManager.getPolyMap().entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			if(rPoly.getSizeOfPixel()>area){
				polyID = id;
				area = rPoly.getSizeOfPixel();
			}
		}
		return polyID;
	}
		
	//Kleinster Punkt in X und Y Richtung wird ermittelt
	private void getMinValue(ResultPolygon rPoly) {
		int minXValue = rPoly.xpoints[0];
		int minYValue = rPoly.ypoints[0];
		for(int i=0;i<rPoly.npoints;i++){	    
	        if (rPoly.xpoints[i] < minXValue) 
	            minXValue = rPoly.xpoints[i];	        
	        if(rPoly.xpoints[i] > minYValue)
	        	minYValue = rPoly.ypoints[i];
	    }
		setXMinPoint(minXValue);
		setYMinPoint(minYValue);
	}
	
	//Grösster Punkt in X und Y Richtung wird ermittelt
	private void getMaxValue(ResultPolygon rPoly) {
		int maxXValue = rPoly.xpoints[0];
		int maxYValue = rPoly.ypoints[0];
		for(int i=0;i<rPoly.npoints;i++){	    
	        if (rPoly.xpoints[i] > maxXValue) 
	            maxXValue = rPoly.xpoints[i];	        
	        if(rPoly.xpoints[i] > maxXValue)
	        	maxYValue = rPoly.ypoints[i];
	    }
		setXMaxPoint(maxXValue);
		setYMaxPoint(maxYValue);
	}
	
	public int areaBoundingBox(){
		System.out.println(getWidthBoundingBox());
		return getWidthBoundingBox() * getLengthBoundingBox();
	}
	
	
	//*******************************************Getter / Setter**********************************************
	public int getLengthBoundingBox() {
		return lengthBoundingBox;
	}

	public void setLengthBoundingBox(int lengthBoundingBox) {
		this.lengthBoundingBox = lengthBoundingBox;
	}

	public int getWidthBoundingBox() {
		return widthBoundingBox;
	}

	public void setWidthBoundingBox(int widthBoundingBox) {
		this.widthBoundingBox = widthBoundingBox;
	}


	public int getXMaxPoint() {
		return xMaxPoint;
	}

	public void setXMaxPoint(int xMaxPoint) {
		this.xMaxPoint = xMaxPoint;
	}

	public int getYMaxPoint() {
		return yMaxPoint;
	}

	public void setYMaxPoint(int yMaxPoint) {
		this.yMaxPoint = yMaxPoint;
	}

	public int getXMinPoint() {
		return xMinPoint;
	}

	public void setXMinPoint(int xMinPoint) {
		this.xMinPoint = xMinPoint;
	}

	public int getYMinPoint() {
		return yMinPoint;
	}

	public void setYMinPoint(int yMinPoint) {
		this.yMinPoint = yMinPoint;
	}
	

}
