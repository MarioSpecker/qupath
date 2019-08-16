package qupath.lib.gui.commands.Mario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Contour {

	
	private int[][] label;
	private HashMap<Integer,Integer> labelAreaMap;			//Id jedes Objekt mit Flächeninhalt Pixel
	private HashMap<Integer,ResultPolygon> polyMap;
	private HashMap<Integer,ArrayList<Integer>> freemanChainMap;
	private HashMap<Integer, Double> circumferenceMap;
	private int[] resultContour;
	private int imgWidth;
	private int imgHeight;
	private BoundaryTracingAlgo bTA;
	private TwoPassAlgo twoPassAlgo;
	private HelperFunctions helperFunctions;
	private int[] argb;
	private final static int SAME_LEVEL = 0;
	private final static int LOWER_LEVEL = 1;
	private final static int HIGHER_LEVEL = 2;
	
	
	public Contour(int imgWidth, int imgHeight, int[] argb){
		this.resultContour = new int[imgWidth*imgHeight];
		this.label= new int[imgHeight][imgWidth];
		this.bTA = new BoundaryTracingAlgo(imgWidth, imgHeight, argb, label, labelAreaMap, resultContour);
		this.twoPassAlgo= new TwoPassAlgo(imgWidth, imgHeight, argb, label);
		this.polyMap = new HashMap<>();
		this.labelAreaMap = new HashMap<>();
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.freemanChainMap = new HashMap<>();
		this.helperFunctions = new HelperFunctions();
		this.argb = argb;
		this.circumferenceMap = new HashMap<>();
	}
	
	
	


	private void createChainForEveryObject(){
		Iterator hmIterator = polyMap.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			System.out.println("+++++++++++++++++++Neues Object++++++++++++++++++++");
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			ArrayList<Integer> chainValueList = new ArrayList<Integer>();
			for(int i=0;i<rPoly.npoints;i++){
				if(rPoly.npoints==1){
					System.out.println("RPoly " + rPoly.npoints);
					chainValueList.add(i,i);
					freemanChainMap.put(id, chainValueList);
					break;
				}else{
					if(i<rPoly.npoints-1){
						int xCurrent= rPoly.xpoints[i];
						int xNew= rPoly.xpoints[i+1];
						int x = checkDirection(xCurrent, xNew);
						int yCurrent= rPoly.ypoints[i];
						int yNew= rPoly.ypoints[i+1];
						int y = checkDirection(yCurrent, yNew);
						chainValueList.add(i,setValueToChain(x, y));
						//System.out.println(chainValueList.get(i));
					}
					else{
						int xCurrent= rPoly.xpoints[i];
						int xNew= rPoly.xpoints[0];
						int x = checkDirection(xCurrent, xNew);
						int yCurrent= rPoly.ypoints[i];
						int yNew= rPoly.ypoints[0];
						int y = checkDirection(yCurrent, yNew);
						chainValueList.add(i,setValueToChain(x, y));
						freemanChainMap.put(id, chainValueList);
					}
				}
			}
			System.out.println("Anzahl Points-> " + rPoly.npoints + "  Anzahl List-> "+chainValueList.size());
		}
	}
	
	
	private int setValueToChain(int x, int y){
		int result = 0;
		if((x==HIGHER_LEVEL)&&(y==SAME_LEVEL))
			result = 0;
		else if((x==HIGHER_LEVEL)&&(y==LOWER_LEVEL))
			result =1;
		else if((x==SAME_LEVEL)&&(y==LOWER_LEVEL))
			result =2;
		else if((x==LOWER_LEVEL)&&(y==LOWER_LEVEL))
			result =3;
		else if((x==LOWER_LEVEL)&&(y==SAME_LEVEL))
			result =4;
		else if((x==LOWER_LEVEL)&&(y==HIGHER_LEVEL))
			result =5;
		else if((x==SAME_LEVEL)&&(y==HIGHER_LEVEL))
			result =6;
		else if((x==HIGHER_LEVEL)&&(y==HIGHER_LEVEL))
			result =7;
		return result;
	}
	
	private int checkDirection(int valueCurrent, int valueNew){
		int result = 0;
		if(valueCurrent==valueNew)
			result = SAME_LEVEL;
		else if(valueCurrent<valueNew)
			result = HIGHER_LEVEL;
		else if(valueCurrent>valueNew)
			result = LOWER_LEVEL;
		return result;
	}
	
	private void calculateCircumference(){
		
	}
	
	
	
	
	//geht jedes einzelene Pixel von dem Label durch und zaehlt so den Flächeninhalt in Pixel
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
	
	
	//Vergleicht die beiden Hashmaps und deren ihr Flächeninhalt miteinander und gibt bei einem Unterschied ein falsch zurueck
	//ansonsten true.....
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
	
	
	//Algo fuer die Darstellung der Kontur
	public void pavlidisAlgo(){
		bTA.labelToArray();
		bTA.searchContourStart(getPolyMap());
		bTA.decreaseArray();
		createChainForEveryObject();
	}
	
	//Algo um die die einzelnen Objekte zu unterscheiden bzw gibt jedem Objekt eine eigenes Label(ID)
	public void twoPass(){
		helperFunctions.invertImage(getImgWidth(), getImgHeight(), getArgb());
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
	
	public int[] getArgb() {
		return argb;
	}

	public void setArgb(int[] argb) {
		this.argb = argb;
	}
}
