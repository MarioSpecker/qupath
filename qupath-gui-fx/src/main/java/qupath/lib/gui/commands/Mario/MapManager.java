package qupath.lib.gui.commands.Mario;

import java.util.ArrayList;
import java.util.HashMap;

public class MapManager {

	private HashMap<Integer,Integer> labelAreaMap;			//Id jedes Objekt mit Flächeninhalt Pixel
	private HashMap<Integer,ResultPolygon> polyMap;			//ObjectID + Polygon
	private HashMap<Integer,ArrayList<Integer>> freemanChainMap;
	private HashMap<Integer, Double> circumferenceMap;		//ID von jedem Object mit Umfang
	
	public MapManager(){
		this.labelAreaMap = new HashMap<>();
		this.polyMap = new HashMap<>();
		this.circumferenceMap = new HashMap<>();
		this.freemanChainMap = new HashMap<>();
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

	
	
}
