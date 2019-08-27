package qupath.lib.gui.commands.Mario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import qupath.lib.geom.Point2;
import qupath.lib.roi.PolygonROI;
import qupath.lib.roi.interfaces.ROI;

public class Contour {

	
	private int[][] label;
	private HashMap<Integer,Integer> labelAreaMap;			//Id jedes Objekts mit Flächeninhalt Pixel
	private HashMap<Integer,ResultPolygon> polyMap;
	private HashMap<Integer,ArrayList<Integer>> freemanChainMap;
	private HashMap<Integer, Double> circumferenceMap;		//ID von jedem Object mit Umfang
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
	private final static double DIAGONAL_LINE = 1.414;
	private final static double STRAIGHT_LINE = 1.0;
	List<Point2> points;
	
	
	

	
	public Contour(int imgWidth, int imgHeight, int[] argb){
		this.resultContour = new int[imgWidth*imgHeight];
		this.label= new int[imgHeight][imgWidth];
		this.bTA = new BoundaryTracingAlgo(imgWidth, imgHeight, argb, label, labelAreaMap, resultContour);
		this.twoPassAlgo= new TwoPassAlgo(imgWidth, imgHeight, argb, label);
		this.polyMap = new HashMap<>();
		this.labelAreaMap = new HashMap<>();
		this.circumferenceMap = new HashMap<>();
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.freemanChainMap = new HashMap<>();
		this.helperFunctions = new HelperFunctions();
		this.argb = argb;
		points = new ArrayList<>();
		
	}
	
	
	//Hier wird die Hashmap<Integer,ArrayList> mit der ObjectID und der dazugehörigen 
	//Liste(Werte fuer Himmelsrichtungen 0..7) befuellt.
	private void createChainForEveryObject(){
		Iterator hmIterator = polyMap.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			boolean isEnd = false;
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			ArrayList<Integer> chainValueList = new ArrayList<Integer>();
			for(int i=0;i<rPoly.npoints;i++){
				if(rPoly.npoints==1){
					chainValueList.add(i,i);
					freemanChainMap.put(id, chainValueList);
					break;
				}else{
					if(i<rPoly.npoints-1)
						addValueToChain(rPoly, i, chainValueList, isEnd);
					else{
						isEnd = true;
						addValueToChain(rPoly, i, chainValueList, isEnd);
						freemanChainMap.put(id, chainValueList);
					}
				}
			}
		}
	}
	
	
	//Werte 0..7 werden hier in Liste eingetragen. Dieser Wert gibt die Richtung von dem naechsten Nachbarpixel wieder
	private void addValueToChain(ResultPolygon rPoly, int i, ArrayList<Integer> chainValueList, boolean isEnd){
		int xCurrent= rPoly.xpoints[i];			//X bzw y wert des aktuellen Pixels
		int yCurrent= rPoly.ypoints[i];
		int xNew, yNew;
		if(!isEnd){							//Werte des NachbarnPixels
			xNew= rPoly.xpoints[i+1];
			yNew= rPoly.ypoints[i+1];
		}
		else								//Werte des NachbarnPixels bzw der Wert wenn man wieder am Startpixel angekommen ist
			xNew= rPoly.xpoints[0];
			yNew= rPoly.ypoints[0];
		int x = checkDirection(xCurrent, xNew);
		int y = checkDirection(yCurrent, yNew);
		chainValueList.add(i,getValueForChain(x, y));			//Werte 0..7 werden der Liste hinzugefügt
	}
	
	
	//Werte 0..7 stellen die Richtungen dar. O -> Osten , 1->Nordosten, 2-> Norden .... . Gibt den
	//jeweiilgen Wert zurueck
	private int getValueForChain(int x, int y){
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
	
	//Um zu prüfen ob das nächste Nachbarpixel auf einer tieferen, gleichen oder höhern Spalte/Zeile liegt
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
	
	//Hier wird der Umfang von jedem Object berechnet.
	private void calculateCircumference(){
		Iterator hmIterator = getFreemanChainMap().entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			double resultCircumference = 0.0;
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ArrayList list = (ArrayList)mapElement.getValue();
			for(int i=0;i<list.size();i++){
				int value = (int) list.get(i);
				if(value==0||value==2||value==4||value==6) //Alle Nachbarpixel die horizontal bzw vertical zu ihren 
					resultCircumference+=STRAIGHT_LINE;				//vorgaengerpixel liegen haben den Umgang 1
				else										//Diagonale haben den Wert 1.414
					resultCircumference+=DIAGONAL_LINE;
			}
			getCircumferenceMap().put(id, resultCircumference);
		}
	}
	
	//Gibt von jedem Objekt die ID und Umfang aus
	private void printCircumferenceFromEveryObject(){
		Iterator hmIterator = getCircumferenceMap().entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			double circumference = (double)mapElement.getValue(); 
			System.out.println("ObjectID-> "+ id + "  Umfang-> " + circumference);
		}
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
	
	
	@SuppressWarnings("unused")
	private void toPolygonROI(){
		Iterator hmIterator = polyMap.entrySet().iterator(); 
		List<Point2> points = new ArrayList<>();
		System.out.println("New Object");
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
			int id = (int)mapElement.getKey();
			ResultPolygon rPoly = (ResultPolygon)mapElement.getValue();
			//ROI roi = new PolygonROI(rPoly.xpoints, rPoly.ypoints, rPoly.npoints, null);
			for (int i = 0; i < rPoly.npoints; i++) {
				float x = (float)rPoly.xpoints[i];
				float y = (float)rPoly.ypoints[i];
				//float y = (float)convertYfromIJ(polygon.ypoints[i], cal, downsampleFactor);
				points.add(new Point2(x, y));
			
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
		calculateCircumference();
		printCircumferenceFromEveryObject();
	}
	
	//Algo um die die einzelnen Objekte zu unterscheiden bzw gibt jedem Objekt eine eigenes Label(ID)
	public void twoPass(){
		helperFunctions.invertImage(getImgWidth(), getImgHeight(), getArgb());
		twoPassAlgo.createFirstStep();
		twoPassAlgo.createSecondStep();
	}
	
	
	public HashMap<Integer, ArrayList<Integer>> getFreemanChainMap() {
		return freemanChainMap;
	}

	public void setFreemanChainMap(
			HashMap<Integer, ArrayList<Integer>> freemanChainMap) {
		this.freemanChainMap = freemanChainMap;
	}

	public HashMap<Integer, Double> getCircumferenceMap() {
		return circumferenceMap;
	}

	public void setCircumferenceMap(HashMap<Integer, Double> circumferenceMap) {
		this.circumferenceMap = circumferenceMap;
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
