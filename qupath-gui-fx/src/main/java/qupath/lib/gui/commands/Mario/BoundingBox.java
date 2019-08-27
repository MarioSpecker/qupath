package qupath.lib.gui.commands.Mario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BoundingBox {
	
	
	private int imgWidth;
	private int imgHeight;
	private int xMaxPoint, yMaxPoint, xMinPoint, yMinPoint;
	private Contour contour;
	
	public BoundingBox(int imgWidth, int imgHeight, int []argb){
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.contour = new Contour(imgWidth, imgHeight, argb);
	}
	
	
	
	
	private void findMinX(){
		Iterator hmIterator = contour.getPolyMap().entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			
			
	}
		
	
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
