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
	private BoundaryTracingAlgo bTA;
	private TwoPassAlgo twoPassAlgo;
	
	public Contour(int imgWidth, int imgHeight, int[] argb){
		this.resultContour = new int[imgWidth*imgWidth];
		this.label= new int[imgHeight][imgWidth];
		this.bTA = new BoundaryTracingAlgo(imgWidth, imgHeight, argb, label, labelAreaMap, resultContour);
		this.twoPassAlgo= new TwoPassAlgo(imgWidth, imgHeight, argb, label);
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
	
	
//	PUBLIC VOID COMPAREAREASIZE(){
//		ITERATOR HMITERATOR = GETPOLYMAP().ENTRYSET().ITERATOR(); 
//		WHILE (HMITERATOR.HASNEXT()) { 
//			MAP.ENTRY MAPELEMENT = (MAP.ENTRY)HMITERATOR.NEXT(); 
//			INT ID = (INT)MAPELEMENT.GETKEY();
//			RESULTPOLYGON RPOLY = (RESULTPOLYGON)MAPELEMENT.GETVALUE();
//			INT NUMBER = RPOLY.NPOINTS;
//			SYSTEM.OUT.PRINTLN("ID= " +ID + "  ANZAHL= "+ NUMBER);
//		}
//		ITERATOR HITERATOR = GETLABELAREAMAP().ENTRYSET().ITERATOR(); 
//		WHILE (HMITERATOR.HASNEXT()) { 
//			MAP.ENTRY MAPELEMENT = (MAP.ENTRY)HITERATOR.NEXT(); 
//			INT ID = (INT)MAPELEMENT.GETKEY();
//			INT NUMBER = (INT)MAPELEMENT.GETVALUE();
//			
//			SYSTEM.OUT.PRINTLN("ID= " +ID + "  ANZAHL= "+ NUMBER);
//		}
//		
//	}
	
	public boolean compareSizeOfArea() {
		Iterator it = labelAreaMap.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	    	int id = (int)pair.getKey();
	    	if(labelAreaMap.get(id)!=polyMap.get(id).sizeOfPixel){
	    		System.out.println("false");
	    		return false;
	    	}
	    }
	    return true;
	}
	
	
	
	public void pavlidisAlgo(){
		bTA.labelToArray();
		bTA.searchContourStart(getPolyMap());
		bTA.decreaseArray();
	}
	
	public void twoPass(){
		twoPassAlgo.createFirstStep();
		twoPassAlgo.createSecondStep();
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
