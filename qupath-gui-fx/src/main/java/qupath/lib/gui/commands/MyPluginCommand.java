package qupath.lib.gui.commands;


import qupath.lib.geom.Point2;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.QuPathGUI.Modes;
import qupath.lib.gui.commands.Mario.BinaryImage;
import qupath.lib.gui.commands.Mario.Contour;
import qupath.lib.gui.commands.Mario.Dilatation;
import qupath.lib.gui.commands.Mario.Erosion;
import qupath.lib.gui.commands.Mario.HelperFunctions;
import qupath.lib.gui.commands.Mario.MapManager;
import qupath.lib.gui.commands.Mario.NearestNeighbor;
import qupath.lib.gui.commands.Mario.ResultPolygon;
import qupath.lib.gui.commands.Mario.TwoPassAlgo;
import qupath.lib.gui.commands.Mario.interfa.Filter;
import qupath.lib.gui.commands.Mario.GaussFilter;
import qupath.lib.gui.commands.Mario.GreyscaleImage;
import qupath.lib.gui.commands.Mario.interfa.Image;
import qupath.lib.gui.commands.Mario.LaPlaceFilter;
import qupath.lib.gui.commands.Mario.interfa.MorphOperations;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.plugins.parameters.BooleanParameter;
import qupath.lib.roi.PolygonROI;
import qupath.lib.roi.interfaces.ROI;
import qupath.lib.rois.*;//ij.gui.PolygonRoi;
//import ij.gui.Roi;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;

import java.util.Arrays;
import java.util.Collections;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;



import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.scene.layout.GridPane;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.shape.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.awt.geom.*;
import java.awt.Polygon;

import javafx.scene.control.ToggleButton;


/**
 * 
 * @author Mario
 *
 */
public class MyPluginCommand implements PathCommand {

	private static int gridSize;
	private Stage dialog;
	private QuPathGUI qupath;
	private int[] argb;
	private static Rectangle[][] rec;
	private boolean[][] kernel;
	private int[] resizedARGB;
	private int[] updatedArray;
	private int[][] laPlaceFilter;
	private VBox vBoxRightBorder;
	private BorderPane root;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btnOk;
	private Button btnCancel;
	private int [][]label;
	private Pane paneCenter;
	private BooleanProperty isTextGrid ;
	private BooleanProperty isColorGrid;
	private ComboBox<String> comboBox;
	private RadioButton radioBtn3; 
	private RadioButton radioBtn5;
	private ToggleGroup tGroup;
	private VBox vBoxLeftBorder;
	private HBox hBox;
	private BufferedImage img;
	private int threshold;
	private static Text[][] textForGrid;
	private static double widthOfGrid;
	private static String[][] lPF3Matrix;
	private static String[][] lPF5Matrix;
	private static String[][] gaussMatrix3x3;
	private static String[][] gaussMatrix5x5;
	private String choiceOperation;
	private int sizeBorder;
	private long alpha[];
	private long red[];
	private long green[];
	private long blue[];
	private boolean drawHist;
	private long maxValueHistogramm;
	private HelperFunctions helperFunftions;

	
	
	
	private String nameEdgeMatrix;
	public static final String NO_OPERATION = "Select";
	public static final String OPERATION_EROSION = "Erosion";
	public static final String OPERATION_DILATATION = "Dilatation";
	public static final String OPERATION_GAUSS = "Gauss";
	public static final String OPERATION_BINARY = "Binary";
	public static final String OPERATION_EDGE = "Edge";
	public static final String OPERATION_GREYSCALE = "Greyscale";
	public static final String OPERATION_CONTOUR = "Contour";
	public static final String OPERATION_POLYPOINTS = "Polypoints";
	public static final String MATRIX_5 = "5er Matrix";
	public static final String MATRIX_3 = "3er Matrix";
	
	public static final double RADIUS_1 = 1.0;
	public static final double RADIUS_2 = 1.5;
	public static final double RADIUS_3 = 2.0;
	public static final double RADIUS_4 = 2.7;



	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
		this.gridSize = 5;
		this.kernel = new boolean[gridSize][gridSize];
		this.rec = new Rectangle[gridSize][gridSize];
		this.textForGrid = new Text[gridSize][gridSize];
		this.widthOfGrid = 250;
		this.lPF3Matrix = new String[][]{{"0","0","0","0","0"},{"0","0","-1","0","0"},{"0","-1","4","-1","0"},{"0","0","-1","0","0"},{"0","0","0","0","0"}};
		this.lPF5Matrix = new String[][]{{"0","0","-1","0","0"},{"0","-1","-2","-1","0"},{"-1","-2","16","-2","-1"},{"0","-1","-2","-1","0"},{"0","0","-1","0","0"}};
		this.gaussMatrix3x3 = new String[][]{{"0","0","0","0","0"},{"0","1","2","1","0"},{"0","2","4","2","0"},{"0","1","2","1","0"},{"0","0","0","0","0"}};
		this.gaussMatrix5x5 = new String[][]{{"1","4","7","4","1"},{"4","16","26","16","4"},{"7","26","41","26","7"},{"4","16","26","16","4"},{"1","4","7","4","1"}};
		this.choiceOperation  = "NOOPERATION";
		this.sizeBorder = 1;
		alpha = new long[256];
		red = new long[256];
		green = new long[256];
		blue =  new long[256];
		drawHist = true;
		this.maxValueHistogramm = 0;
		isTextGrid = new SimpleBooleanProperty(false);
		isColorGrid = new SimpleBooleanProperty(false);
		this.nameEdgeMatrix = "3er matrix";
		
	}

	


	@SuppressWarnings("restriction")
	@Override
	public void run() {
		//Versuch zum Histogramm
				
		initNodes();
		setViewToDefault();
		img = qupath.getViewer().getThumbnail(); // create Image
		argb = new int[getImg().getHeight() * getImg().getWidth()];
		this.label = new int[getImg().getHeight()][getImg().getWidth()];
		updatedArray = new int[getImg().getHeight() * getImg().getWidth()];
		getImg().getRGB(0, 0, getImg().getWidth(), getImg().getHeight(), getArgb(), 0, getImg().getWidth());
		helperFunftions = new HelperFunctions();
		

		System.arraycopy(getArgb(), 0, getUpdatedArray(), 0, getArgb().length);
		setThreshold(getIterativeThreshold(getArgb(), getImg().getWidth(), getImg().getHeight()));
		
		dialog = createDialog();
		getDialog().setOnCloseRequest(event ->
	    {
	        this.setChoiceOperation(NO_OPERATION);
	        getDialog().close();
	    });
		getDialog().showAndWait();
		

		//Nach Bestätigung des Ok Knopfes bzw nach schliessen des Fensters wird die Ausgewaehlte Operation durchgeführt 
		switch(getChoiceOperation()){
		case OPERATION_BINARY:
			Image grey = new GreyscaleImage();
			grey.convertImage(getImg().getWidth(), getImg().getHeight(),  getArgb(), 0);
			setThreshold(getIterativeThreshold(getArgb(), getImg().getWidth(), getImg().getHeight()));
			Image binary = new BinaryImage();
			binary.convertImage(getImg().getWidth(), getImg().getHeight(),  getArgb(), getThreshold());
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case OPERATION_GREYSCALE:
			//Hier wird aus einem Farbbild ein Graustufenbild erzeugt
			Image greyscale = new GreyscaleImage();
			greyscale.convertImage(getImg().getWidth(), getImg().getHeight(), getArgb(), 0);
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case OPERATION_DILATATION:
			BufferedImage dilatationImage = new BufferedImage(getImg().getWidth() + 4, getImg().getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
			int[] dilatArray = new int[dilatationImage.getHeight() * dilatationImage.getWidth()];
			MorphOperations dilatation = new Dilatation();
			dilatation.prepareMorphOperation(dilatationImage, dilatArray, getArgb(), getImg().getWidth(), getImg().getHeight());
			dilatation.executeMorpOperation(dilatationImage.getWidth(), dilatationImage.getHeight(), dilatArray, getArgb(), getImg().getWidth(), getHalfKernelSize(), getKernel());			
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case OPERATION_EROSION:
			//Erosion erhät man wenn man auf ein Binärbild eine inversion durchführt, darauf dann eine Dilatation und
			//zum Schluss nochmal eine Inversion
			helperFunftions.invertImage(getImg().getWidth(), getImg().getHeight(), getArgb());
			BufferedImage erosionImage = new BufferedImage(getImg().getWidth() + 4, getImg().getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
			int[] erosionArray = new int[erosionImage.getHeight() * erosionImage.getWidth()];
			MorphOperations dilatation1 = new Dilatation();
			dilatation1.prepareMorphOperation(erosionImage, erosionArray, getArgb(), getImg().getWidth(), getImg().getHeight());
			dilatation1.executeMorpOperation(erosionImage.getWidth(), erosionImage.getHeight(), erosionArray, getArgb(), getImg().getWidth(), getHalfKernelSize(), getKernel());			
			helperFunftions.invertImage(img.getWidth(), img.getHeight(), getArgb());
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case OPERATION_GAUSS:
			//Um das Bild zu gläten
			Filter gauss = new GaussFilter();
			gauss.filterOperation(getImg().getWidth(), getImg().getHeight(), getHalfKernelSize(), getGridSize(), getArgb(), getUpdatedArray());
			drawImage(getImg().getHeight(), getImg().getWidth(), getUpdatedArray());
			break;
		case OPERATION_EDGE:
			//
			Filter laPlace = new LaPlaceFilter();
			laPlace.filterOperation(getImg().getWidth(), getImg().getHeight(), getSizeBorder(), getGridSize(), getArgb(), getUpdatedArray());
			drawImage(getImg().getHeight(), getImg().getWidth(), getUpdatedArray());
			break;
		
		case OPERATION_CONTOUR:
			//Diese Contourverfolgung funktioniert nur bei Anwendung auf ein Binärbild
			MapManager mapManger = new MapManager();
			Contour c = new Contour(getImg().getWidth(), getImg().getHeight(), getArgb(), mapManger);
			
			//c.twoPass();
			c.pavlidisAlgo();
			//System.out.println("In der Main " + mapManger.getPolyMap());
			//if(c.compareSizeOfArea()){
				drawImage(getImg().getHeight(), getImg().getWidth(),c.getResultContour());
			//}
			break;
		case OPERATION_POLYPOINTS:
//			NearestNeighbor nearestNeighbour = new NearestNeighbor(getImg().getWidth(), getImg().getHeight(), getArgb(), polyMap, labelAreaMap);
//			nearestNeighbour.z();
			break;
		
			
		case NO_OPERATION:
			break;
			
		}
		
	}
		
		
	

	private void drawImage(int height, int width, int[] rgb) {
		qupath.getViewer().getThumbnail().setRGB(0, 0, width, height, rgb, 0, width);
		qupath.getViewer().repaintEntireImage();
	}


	//Schwellenwertberechnung
	private int getIterativeThreshold(int[] argb, int width, int height) {
		long totalSmallerThreshold = 0;
		long totalTallerThreshold = 0;
		long countPixelSmaller = 0;
		long countPixelTaller = 0;
		int currentThreshold = 100;
		int newThreshold = 200;
		float averageSmallerThreshold = 0;
		float averageTallerThreshold = 0;
		do {
			currentThreshold = newThreshold;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					int pix = argb[pos] & 0xff;
					if (pix <= currentThreshold) {
						totalSmallerThreshold += pix;
						countPixelSmaller++;
					} else {
						totalTallerThreshold += pix;
						countPixelTaller++;
					}
				}
			}
			averageSmallerThreshold = totalSmallerThreshold / countPixelSmaller;
			averageTallerThreshold = totalTallerThreshold / countPixelTaller;
			float x = (averageSmallerThreshold + averageTallerThreshold) / 2;
			newThreshold = (int) x;
		} while (Math.abs((newThreshold - currentThreshold)) >= 1);
		return newThreshold;
	}

	private boolean[][] createKernel(double radius) {
		boolean[][] k = new boolean[getGridSize()][getGridSize()];
		int x = 0, y = 0;
		for (int j = -2; j <= 2; j++) {
			for (int i = -2; i <= 2; i++) {
				double r = Math.sqrt((Math.pow(i, 2) + Math.pow(j, 2)));
				if (r > radius) {
					k[x][y] = false;
				} else {
					k[x][y] = true;
				}
				y++;
			}
			y = 0;
			x++;
		}
		return k;
	}
	
	
	
	private boolean isBinary(){
		for(int y =0; y< getImg().getHeight(); y++){
			for(int x=0;x<getImg().getWidth(); x++){
				int pos = y*getImg().getWidth()+x;
				int r = getArgb()[pos]>>16&0xff;
				int g = getArgb()[pos]>>8&0xff;
				int b = getArgb()[pos]&0xff;
				if((r=g=b)!=(0)||(r=g=b)!=(255))
					return false;
			}
		}
		return true;
	}
	
	private boolean isGreyscale(){
		for(int y =0; y< getImg().getHeight(); y++){
			for(int x=0;x<getImg().getWidth(); x++){
				int pos = y*getImg().getWidth()+x;
				int r = getArgb()[pos]>>16&0xff;
				int g = getArgb()[pos]>>8&0xff;
				int b = getArgb()[pos]&0xff;
				if((r!=g)&&(g!=b))
				return false;
			}
		}
		return true;
	}
	
	

//	public void printKernel(boolean[][] kernel) {
//		for (boolean[] xS : kernel) {
//			for (boolean v : xS) {
//
//				System.out.println(v);
//			}
//			System.out.println();
//		}
//
//	}
//	
	
	
		


	

	

	// ++++++++++++++++++++++++++++++++++++++Graphic....+++++++++++++++++++++++++++++++++++++++++

	
	//
	@SuppressWarnings("restriction")
	protected Stage createDialog() {
		Stage dialog = new Stage();
		dialog.initOwner(qupath.getStage());
		dialog.setTitle("My Plugin Dialog");
		Scene scene = new Scene(addBorderPane(), 700, 400);		
		scene.getStylesheets().add("css/StyleMario.css");
		dialog.setScene(scene);
		
		return dialog;
	}

	@SuppressWarnings("restriction")
	private BorderPane addBorderPane() {
		createCenter();
		createCenter();
		createLeftBorder();
		createRightBorder();
		createBottom();
		return root;
	}
	
	
	
	@SuppressWarnings("restriction")
	private void createRightBorder(){
		getComboBox().getItems().add(NO_OPERATION);
		getComboBox().getItems().add(OPERATION_GREYSCALE);
		getComboBox().getItems().add(OPERATION_BINARY); 
		getComboBox().getItems().add(OPERATION_EDGE); 
		getComboBox().getItems().add(OPERATION_GAUSS); 
		getComboBox().getItems().add(OPERATION_DILATATION); 
		getComboBox().getItems().add(OPERATION_EROSION); 
		getComboBox().getItems().add(OPERATION_CONTOUR);
		getComboBox().getItems().add(OPERATION_POLYPOINTS);
		getComboBox().getSelectionModel().select(0);
		getComboBox().valueProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String old, String selected) {
				//Bei Auswahl eines Items aus der ComboBox 
				selectComboBoxOperation(selected);
			}    
		});
		getradioBtn3().setText(MATRIX_3);
		getradioBtn5().setText(MATRIX_5);
		tGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	           @Override
	           public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	               if (tGroup.getSelectedToggle() != null) {
	                   RadioButton button = (RadioButton) gettGroup().getSelectedToggle();
	                   if(button.getText().contains(MATRIX_3)){
	                	   setSizeBorder(1);
	                	   cleanGrid();	                	   
	                	   setNameEdgeMatrix(MATRIX_3);
	                	   triggerIsTextGridProperty();
	                	   
	                   }else{
	                	   setSizeBorder(2);
	                	   setNameEdgeMatrix(MATRIX_5);
	                	   triggerIsTextGridProperty();
	                   }
	               }
	           }
	       });
		getradioBtn3().setToggleGroup(tGroup);
		getradioBtn5().setToggleGroup(tGroup);
		getvBoxRightBorder().setPadding(new Insets(10,10,10,10));
		getvBoxRightBorder().setMargin(getradioBtn3(), new Insets(15,5,5,0));
		getvBoxRightBorder().setMargin(getradioBtn5(), new Insets(5,5,5,0));
		getvBoxRightBorder().getChildren().addAll(getComboBox(),getradioBtn3(),getradioBtn5());
		root.setRight(getvBoxRightBorder());
	}
	
	
	//Auswahl aller Operationen
	private void selectComboBoxOperation(String selected){
		if(selected.contains(OPERATION_EDGE)){
			updateViewEdge();
			drawHist = false;
			createCenter();
			setChoiceOperation(OPERATION_EDGE);
			triggerIsTextGridProperty();
		}
		else if(selected.contains(OPERATION_GAUSS)){
			updateViewForNonSelectableOperations();
			drawHist = false;
			createCenter();
			setChoiceOperation(OPERATION_GAUSS);
			triggerIsTextGridProperty();
		}
		else if(selected.contains(OPERATION_POLYPOINTS)){
			setChoiceOperation(OPERATION_POLYPOINTS);
		}
		else if(selected.contains(OPERATION_DILATATION)){
			updateViewMorph();
			createCenter();
			setChoiceOperation(OPERATION_DILATATION);
			triggerIsColorGridProperty();
		}
		else if(selected.contains(OPERATION_EROSION)){
			updateViewMorph();
			createCenter();
			setChoiceOperation(OPERATION_EROSION);
			triggerIsColorGridProperty();
		}
		else if(selected.contains(OPERATION_BINARY)){
			updateViewForNonSelectableOperations();
			drawHist=true;
			createCenter();
			setChoiceOperation(OPERATION_BINARY);
		}
		else if(selected.contains(OPERATION_GREYSCALE)){
			updateViewForNonSelectableOperations();
			drawHist=true;
			createCenter();
			setChoiceOperation(OPERATION_GREYSCALE);
		}
		else if(selected.contains(OPERATION_CONTOUR)){
			updateViewForNonSelectableOperations();
			drawHist=true;
			createCenter();
			setChoiceOperation(OPERATION_CONTOUR);
		}
		else if(selected.contains(NO_OPERATION)){
			updateViewEdge();
			drawHist=true;
			createCenter();
			setChoiceOperation(NO_OPERATION);
		}
	}
	
	//GUI update wenn Dilatation bzw Erosion ausgewaehlt wird
	@SuppressWarnings("restriction")
	private void updateViewMorph(){
		getvBoxLeftBorder().setDisable(false);
		getradioBtn3().setDisable(true);
		getradioBtn5().setDisable(true);
		setSizeBorder(1);
		drawHist=false;
	}
	
	//GUI update wenn Graustufenbild, Binärbild oder GaussFilter angewendet wird
	@SuppressWarnings("restriction")
	private void updateViewForNonSelectableOperations(){
		getradioBtn3().setDisable(true);
		getradioBtn5().setDisable(true);
		getvBoxLeftBorder().setDisable(true);
		setSizeBorder(2);
	}
	
	@SuppressWarnings("restriction")
	private void updateViewEdge(){
		getvBoxLeftBorder().setDisable(true);
		getradioBtn3().setSelected(true);
		getradioBtn3().setDisable(false);
		getradioBtn5().setDisable(false);
		setSizeBorder(1);
	}
	

	
	
	@SuppressWarnings("restriction")
	private void createCenter(){
		paneCenter = makePaneCenter(getGridSize());
		getRoot().setAlignment(paneCenter, Pos.CENTER);
		getRoot().setMargin(paneCenter, new Insets(20, 20, 20, 20));
		getRoot().setCenter(paneCenter);
	}
	
	
	
	@SuppressWarnings("restriction")
	private void createBottom(){
		getBtnOk().setText("OK");
		getBtnOk().setOnAction(actionEvent -> {
			getDialog().close();
		});
		gethBox().setAlignment(Pos.CENTER);
		gethBox().setPadding(new Insets(10,10,10,10));
		gethBox().setHgrow(getBtnOk(), Priority.ALWAYS);
		getBtnOk().setMaxWidth(Double.MAX_VALUE);
		gethBox().getChildren().add(getBtnOk());
		getRoot().setBottom(gethBox());
	}
	
	
	//Linker Rand des BorderPane´s wird hier erstellt
	@SuppressWarnings("restriction")
	private void createLeftBorder(){
		getBtn1().setText("Button 1");
		getBtn1().setOnAction(actionEvent -> {
			setKernel(this.createKernel(this.RADIUS_1));
			this.fillGridWithColor();
		});
		getBtn2().setText("Button 2");
		getBtn2().setOnAction(actionEvent -> {
			setKernel(this.createKernel(this.RADIUS_2));
			this.fillGridWithColor();
		});
		getBtn3().setText("Button 3");
		getBtn3().setOnAction(actionEvent -> {
			setKernel(this.createKernel(this.RADIUS_3));
			this.fillGridWithColor();
		});
		getBtn4().setText("Button 4");
		getBtn4().setOnAction(actionEvent -> {
			setKernel(this.createKernel(this.RADIUS_4));
			this.fillGridWithColor();
		});
		getvBoxLeftBorder().setVgrow(btn1, Priority.ALWAYS);
		getvBoxLeftBorder().setVgrow(btn2, Priority.ALWAYS);
		getvBoxLeftBorder().setVgrow(btn3, Priority.ALWAYS);
		getvBoxLeftBorder().setVgrow(btn4, Priority.ALWAYS);
		getBtn1().setMaxHeight(Double.MAX_VALUE);
		getBtn2().setMaxHeight(Double.MAX_VALUE);
		getBtn3().setMaxHeight(Double.MAX_VALUE);
		getBtn4().setMaxHeight(Double.MAX_VALUE);
		getvBoxLeftBorder().getChildren().addAll(getBtn1(), getBtn2(),getBtn3(), getBtn4());
		getvBoxLeftBorder().setPadding(new Insets(10,10,10,10));
		getRoot().setLeft(getvBoxLeftBorder());
	}
	
	
	//Bei Dilatation und Erosion wird der Kernel mit Farbe auf das Grid gezeichnet
	private void fillGridWithColor(){
		this.getPaneCenter().widthProperty().addListener(e->{
			this.fillGridKernel(this.getKernel());
		});
		this.getPaneCenter().heightProperty().addListener(e->{
			this.fillGridKernel(this.getKernel());
		});
		this.fillGridKernel(this.getKernel());
	}

	private void fillGridKernel(boolean[][] kernel) {
		for (int i = 0; i < getGridSize(); i++) {
			for (int j = 0; j < getGridSize(); j++) {
				if (getKernel()[i][j] == true) {
					getRec()[i][j].setFill(Color.RED);
				} else {
					getRec()[i][j].setFill(Color.WHITE);
				}
			}
		}
	}
	
	
	//Hier wird das Grid erstellt
	@SuppressWarnings("restriction")
	public Pane makePaneCenter(int size) {
		Pane pane = new Pane();
		if(drawHist){
			fillRGBWithValues();
			LineChart<String, Number> histo= drawHistogram();
			pane.getChildren().add(histo);
			histo.prefHeightProperty().bind(pane.heightProperty());
			histo.prefWidthProperty().bind(pane.widthProperty());
		}
		else{
			pane.heightProperty().addListener(e->{
				adjustGrid(getPaneCenter().getWidth(), getPaneCenter().getHeight(), size, pane);
				});
			pane.widthProperty().addListener(e->{
				adjustGrid(getPaneCenter().getWidth(), getPaneCenter().getHeight(), size, pane);
				});			
		}
		return pane;
	}
	
	//Grid wird der Graphic angepasst 
	private void adjustGrid(double widthPane, double heightPane, int size, Pane pane){
		double width = widthPane / size;
		double height = heightPane/size;
		pane.getChildren().clear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				getTextForGrid()[i][j] = new Text("");
				getRec()[i][j] = new Rectangle();
				getRec()[i][j].setX(i * width);
				getRec()[i][j].setY(j * height);
				getRec()[i][j].setWidth(width);
				getRec()[i][j].setHeight(height);
				getRec()[i][j].setFill(null);
				getRec()[i][j].setStroke(Color.BLACK);
				pane.getChildren().addAll(getRec()[i][j],getTextForGrid()[i][j]);}}
	}
	
	
	
	//Die einzelnen Rechtecke/Panes des Grids werden mit Zahlen befuellt
	@SuppressWarnings("restriction")
	private void fillGridWithText(String nameMatrix, String nameOperation){
		getPaneCenter().widthProperty().addListener(e->{
			textToGrid(nameMatrix, nameOperation);
		});
		getPaneCenter().heightProperty().addListener(e->{
			textToGrid(nameMatrix, nameOperation);
		});
		double width = getPaneCenter().getWidth() / getGridSize();
			textToGrid(nameMatrix, nameOperation);
	}
	
	private void textToGrid(String nameMatrix, String nameOperation){
		for (int i = 0; i < getGridSize(); i++) {
			for (int j = 0; j < getGridSize(); j++) {
				getTextForGrid()[i][j].setX(i *getRectangleWidth()+17);
				getTextForGrid()[i][j].setY(j*getRectangleHeight()+30);
				getTextForGrid()[i][j].setFont(Font.font ("Verdana", 20));
				if(nameMatrix.contains(MATRIX_3)&&nameOperation.contains(OPERATION_EDGE))
					getTextForGrid()[i][j].setText(getlPF3Matrix()[i][j]);
				else if (nameMatrix.contains(MATRIX_5)&&nameOperation.contains(OPERATION_EDGE))
					getTextForGrid()[i][j].setText(getlPF5Matrix()[i][j]);
				else if (nameMatrix.contains(MATRIX_5)&&nameOperation.contains(OPERATION_GAUSS)){
					getTextForGrid()[i][j].setText(getGaussMatrix5x5()[i][j]);
				}					
			}
		}
	}
	
	
	//Das Grid in der GUI wird gelöscht bzw alle Zahlen und Farben gelöscht
	@SuppressWarnings("restriction")
	private void cleanGrid(){
		for (int i = 0; i < getGridSize(); i++) {
			for (int j = 0; j < getGridSize(); j++) {
				//getRec()[i][j].setFill(Color.WHITE);
				getTextForGrid()[i][j].setText("");
			}
		}
	}
	
	// LineChart wird erstellt 
	public LineChart<String, Number> drawHistogram(){
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final LineChart<String, Number> chartHistogram
		= new LineChart<>(xAxis, yAxis);
		chartHistogram.getXAxis().setAutoRanging(true);
		chartHistogram.getYAxis().setAutoRanging(true);
		chartHistogram.setCreateSymbols(false);
		XYChart.Series seriesRed= new XYChart.Series();
		seriesRed.setName("Red");
		XYChart.Series seriesGreen= new XYChart.Series();
		seriesGreen.setName("Green");
		XYChart.Series seriesBlue= new XYChart.Series();
		seriesBlue.setName("Blue");
		XYChart.Series seriesThreshold= new XYChart.Series();
		seriesThreshold.setName("Threshold");
		for(int i=0; i<red.length;i++){
			seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
			seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
			seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
			if(getMaxValueHistogramm() < getRed()[i]) 
		         setMaxValueHistogramm(getRed()[i]);
		    if(getMaxValueHistogramm() < getGreen()[i]) 
		         setMaxValueHistogramm(getGreen()[i]);
		    if(getMaxValueHistogramm() < getBlue()[i]) 
		         setMaxValueHistogramm(getBlue()[i]);
		}
   		seriesThreshold.getData().add(new XYChart.Data(String.valueOf(getThreshold()), 0));
		seriesThreshold.getData().add(new XYChart.Data(String.valueOf(getThreshold()), getMaxValueHistogramm()));
		chartHistogram.getData().addAll(seriesRed, seriesGreen, seriesBlue, seriesThreshold);
		return chartHistogram;
	}
	
	
	private void fillRGBWithValues(){
		for (int i = 0; i < 256; i++) {
			alpha[i] = red[i] = green[i] = blue[i] = 0;
		}
		for(int y =0; y< getImg().getHeight(); y++){
			for(int x=0;x<getImg().getWidth(); x++){
				int pos = y*getImg().getWidth()+x;
				int r = getArgb()[pos]>>16&0xff;
				int g = getArgb()[pos]>>8&0xff;
				int b = getArgb()[pos]&0xff;
				getRed()[r]++;
				getGreen()[g]++;
				getBlue()[b]++;
			}
		}
	}
	
	
	//Wird beim Start des Plugins aufgerufen. Die Gui wird auf default gestellt
	@SuppressWarnings("restriction")
	private void setViewToDefault(){
		getvBoxLeftBorder().setDisable(true);
		getradioBtn3().setDisable(true);
		getradioBtn5().setDisable(true);
		
	}

	//Alle Nodes des Plugins werden hier initialisiert
	@SuppressWarnings("restriction")
	private void initNodes(){
		vBoxRightBorder = new VBox();
		root = new BorderPane();
		btn1 = new Button();
		btn2 = new Button();
		btn3 = new Button();
		btn4 = new Button();
		btnOk = new Button();
		comboBox = new ComboBox<String>();
		radioBtn3 = new RadioButton();
		radioBtn5 = new RadioButton();
		vBoxLeftBorder = new VBox(15);
		tGroup = new ToggleGroup();
		hBox = new HBox();
		
		
		setKernel(this.createKernel(RADIUS_1));
		for(int i=0;i<getGridSize();i++ ){			//
			for(int j=0;j<getGridSize();j++ ){
				getTextForGrid()[i][j] = new Text("");
				getRec()[i][j] = new Rectangle();
			}
		}		
	
		//Immer wenn die BooleanProperty textGridProperty geandert wird, wird das Grid mit den dazugehörigen zahlen befuellt
		setNameEdgeMatrix(MATRIX_3);
		this.isTextGridProperty().addListener(e->{
				if((getNameEdgeMatrix().contains(MATRIX_3))&&(getChoiceOperation().contains(OPERATION_EDGE)))
					fillGridWithText(MATRIX_3, getChoiceOperation());
				else if((getNameEdgeMatrix().contains(MATRIX_5))&&(getChoiceOperation().contains(OPERATION_EDGE)))
					fillGridWithText(MATRIX_5, getChoiceOperation());
				else if((getChoiceOperation().contains(OPERATION_GAUSS)))
					fillGridWithText(MATRIX_5, getChoiceOperation());
		});
		

		this.isColorGridProperty().addListener(e->{
		this.fillGridWithColor();
		});
	}
	
	
//********************************HILFSFUNKTIONEN******************************************************************
	
	//Aendert den boolean  damit die textGridProperty ausgelöst wird
	private void triggerIsTextGridProperty(){
		if(this.getIsTextGrid()==true)
			this.isTextGrid.set(false);
		else
			this.isTextGrid.set(true);
	}
	
	private void triggerIsColorGridProperty(){
		if(this.getIsColorGrid()==true)
			this.isColorGrid.set(false);
		else
			this.isColorGrid.set(true);
	}
	
	
	private double getRectangleWidth(){
		return getPaneCenter().getWidth() / getGridSize();
	}
	
	private double getRectangleHeight(){
		return getPaneCenter().getHeight()/getGridSize();
	}
		
		
		

	
		
	//++++++++++++++++++++++++++++++++++++++Getter // Setter+++++++++++++++++++++++++

	
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public int[] getArgb() {
		return argb;
	}

	public void setArgb(int[] argb) {
		this.argb = argb;
	}

	public boolean[][] getKernel() {
		return kernel;
	}

	public void setKernel(boolean[][] kernel) {
		this.kernel = kernel;
	}

	

	public int[][] getLaPlaceFilter() {
		return laPlaceFilter;
	}

	public void setLaPlaceFilter(int[][] laPlaceFilter) {
		this.laPlaceFilter = laPlaceFilter;
	}
	
	public static int getGridSize() {
		return gridSize;
	}

	public static void setGridSize(int gridSize) {
		MyPluginCommand.gridSize = gridSize;
	}

	public Stage getDialog() {
		return dialog;
	}

	public void setDialog(Stage dialog) {
		this.dialog = dialog;
	}

	public QuPathGUI getQupath() {
		return qupath;
	}

	public void setQupath(QuPathGUI qupath) {
		this.qupath = qupath;
	}

	public static Rectangle[][] getRec() {
		return rec;
	}

	public static void setRec(Rectangle[][] rec) {
		MyPluginCommand.rec = rec;
	}

	public int[] getResizedARGB() {
		return resizedARGB;
	}

	public void setResizedARGB(int[] resizedARGB) {
		this.resizedARGB = resizedARGB;
	}



	@SuppressWarnings("restriction")
	public VBox getvBoxRightBorder() {
		return vBoxRightBorder;
	}

	public void setvBoxRightBorder(VBox vBoxRightBorder) {
		this.vBoxRightBorder = vBoxRightBorder;
	}


	@SuppressWarnings("restriction")
	public VBox getvBoxLeftBorder() {
		return vBoxLeftBorder;
	}

	public void setvBoxLeftBorder(VBox vBox) {
		this.vBoxLeftBorder = vBox;
	}



	@SuppressWarnings("restriction")
	public BorderPane getRoot() {
		return root;
	}

	public void setRoot(BorderPane root) {
		this.root = root;
	}

	@SuppressWarnings("restriction")
	public Button getBtn1() {
		return btn1;
	}

	public void setBtn1(Button btn1) {
		this.btn1 = btn1;
	}

	@SuppressWarnings("restriction")
	public Button getBtn2() {
		return btn2;
	}

	public void setBtn2(Button btn2) {
		this.btn2 = btn2;
	}

	@SuppressWarnings("restriction")
	public Button getBtn3() {
		return btn3;
	}

	public void setBtn3(Button btn3) {
		this.btn3 = btn3;
	}

	public Button getBtn4() {
		return btn4;
	}

	public void setBtn4(Button btn4) {
		this.btn4 = btn4;
	}

	public Button getBtnOk() {
		return btnOk;
	}

	public void setBtnOk(Button btnOk) {
		this.btnOk = btnOk;
	}

	public ComboBox<String> getComboBox() {
		return comboBox;
	}

	public void setComboBox(ComboBox<String> comboBox) {
		this.comboBox = comboBox;
	}

	public RadioButton getradioBtn3() {
		return radioBtn3;
	}

	public void setradioBtn3(RadioButton radioBtn3) {
		this.radioBtn3 = radioBtn3;
	}

	public RadioButton getradioBtn5() {
		return radioBtn5;
	}

	public void setradioBtn5(RadioButton radioBtn5) {
		this.radioBtn5 = radioBtn5;
	}

	public ToggleGroup gettGroup() {
		return tGroup;
	}

	public void settGroup(ToggleGroup tGroup) {
		this.tGroup = tGroup;
	}
	

	public HBox gethBox() {
		return hBox;
	}

	public void sethBox(HBox hBox) {
		this.hBox = hBox;
	}
	
	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	
	public static Text[][] getTextForGrid() {
		return textForGrid;
	}

	public static void setTextForGrid(Text[][] textForGrid) {
		MyPluginCommand.textForGrid = textForGrid;
	}
	
	public static double getWidthOfGrid() {
		return widthOfGrid;
	}


	public void setWidthOfGrid(double d) {
		this.widthOfGrid = d;
	}


	public static String[][] getlPF5Matrix() {
		return lPF5Matrix;
	}


	public static void setlPF5Matrix(String[][] is) {
		MyPluginCommand.lPF5Matrix = is;
	}


	public static String[][] getlPF3Matrix() {
		return lPF3Matrix;
	}

	public static void setlPF3Matrix(String[][] lPF3Matrix) {
		MyPluginCommand.lPF3Matrix = lPF3Matrix;
	}
	
	private int getHalfKernelSize() {
		return 2;
	}
	
	public String getChoiceOperation() {
		return choiceOperation;
	}

	public void setChoiceOperation(String choiceOperation) {
		this.choiceOperation = choiceOperation;
	}
	
	public static String[][] getGaussMatrix3x3() {
		return gaussMatrix3x3;
	}

	public static void setGaussMatrix3x3(String[][] gaussMatrix3x3) {
		MyPluginCommand.gaussMatrix3x3 = gaussMatrix3x3;
	}

	public static String[][] getGaussMatrix5x5() {
		return gaussMatrix5x5;
	}


	public static void setGaussMatrix5x5(String[][] gaussMatrix5x5) {
		MyPluginCommand.gaussMatrix5x5 = gaussMatrix5x5;
	}
	
	public int getSizeBorder() {
		return sizeBorder;
	}


	public void setSizeBorder(int sizeBorder) {
		this.sizeBorder = sizeBorder;
	}
	
	public int[] getUpdatedArray() {
		return updatedArray;
	}

	public void setUpdatedArray(int[] updatedArray) {
		this.updatedArray = updatedArray;
	}
	
	public Button getBtnCancel() {
		return btnCancel;
	}

	public void setBtnCancel(Button btnCancel) {
		this.btnCancel = btnCancel;
	}
	
	public int[][] getLabel() {
		return label;
	}

	public void setLabel(int[][] label) {
		this.label = label;
	}
	
	public Pane getPaneCenter() {
		return paneCenter;
	}

	public void setPaneCenter(Pane paneCenter) {
		this.paneCenter = paneCenter;
	}
	
	public long getMaxValueHistogramm() {
		return maxValueHistogramm;
	}

	public void setMaxValueHistogramm(long red2) {
		this.maxValueHistogramm = red2;
	}
	
	public long[] getAlpha() {
		return alpha;
	}

	public void setAlpha(long[] alpha) {
		this.alpha = alpha;
	}

	public long[] getRed() {
		return red;
	}

	public void setRed(long[] red) {
		this.red = red;
	}

	public long[] getGreen() {
		return green;
	}

	public void setGreen(long[] green) {
		this.green = green;
	}

	public long[] getBlue() {
		return blue;
	}

	public void setBlue(long[] blue) {
		this.blue = blue;
	}
	

	public String getNameEdgeMatrix() {
		return nameEdgeMatrix;
	}

	public void setNameEdgeMatrix(String nameEdgeMatrix) {
		this.nameEdgeMatrix = nameEdgeMatrix;
	}
	
	public final Boolean getIsTextGrid() {
		return isTextGrid.get();
	}

	public final void setIsTextGrid(Boolean text) {
		this.isTextGrid.set(text);
	}

	public BooleanProperty isTextGridProperty() {
		return isTextGrid ;
	}
	
	public final Boolean getIsColorGrid() {
		return isColorGrid.get();
	}

	public final void setIsColorGrid(Boolean color) {
		this.isColorGrid.set(color);
	}

	public BooleanProperty isColorGridProperty() {
		return isColorGrid ;
	}
	
	
	
	
}
