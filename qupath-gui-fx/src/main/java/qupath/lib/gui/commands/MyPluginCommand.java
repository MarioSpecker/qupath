package qupath.lib.gui.commands;


import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.Mario.BinaryImage;
import qupath.lib.gui.commands.Mario.Contour;
import qupath.lib.gui.commands.Mario.Dilatation;
import qupath.lib.gui.commands.Mario.Erosion;
import qupath.lib.gui.commands.Mario.TwoPassAlgo;
import qupath.lib.gui.commands.Mario.interfa.Filter;
import qupath.lib.gui.commands.Mario.GaussFilter;
import qupath.lib.gui.commands.Mario.GreyscaleImage;
import qupath.lib.gui.commands.Mario.interfa.Image;
import qupath.lib.gui.commands.Mario.LaPlaceFilter;
import qupath.lib.gui.commands.Mario.interfa.MorphOperations;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.helpers.DisplayHelpers;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
	public BooleanProperty bearb;
	

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
		bearb = new SimpleBooleanProperty();
		
	}

	


	@SuppressWarnings("restriction")
	@Override
	public void run() {
		initNodes();
		setViewToDefault();
		
		img = qupath.getViewer().getThumbnail(); // create Image
		argb = new int[getImg().getHeight() * getImg().getWidth()];
		this.label = new int[getImg().getHeight()][getImg().getWidth()];
		updatedArray = new int[getImg().getHeight() * getImg().getWidth()];
		getImg().getRGB(0, 0, getImg().getWidth(), getImg().getHeight(), getArgb(), 0, getImg().getWidth());
		
		//Versuch zum Histogramm
		

		System.arraycopy(getArgb(), 0, getUpdatedArray(), 0, getArgb().length);
		setThreshold(getIterativeThreshold(getArgb(), getImg().getWidth(), getImg().getHeight()));
		
		dialog = createDialog();
		getDialog().setOnCloseRequest(event ->
	    {
	        this.setChoiceOperation("NOOPERATION");
	        getDialog().close();
	    });
		getDialog().showAndWait();
		

		//Nach Bestätigung des Ok Knopfes bzw nach schliessen des Fensters wird die Ausgewaehlte Operation durchgeführt 
		switch(getChoiceOperation()){
		case "BINARY":
			Image grey = new GreyscaleImage();
			grey.convertImage(getImg().getWidth(), getImg().getHeight(),  getArgb(), 0);
			setThreshold(getIterativeThreshold(getArgb(), getImg().getWidth(), getImg().getHeight()));
			Image binary = new BinaryImage();
			binary.convertImage(getImg().getWidth(), getImg().getHeight(),  getArgb(), getThreshold());
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case "GRAYSCALE":
			//Hier wird aus einem Farbbild ein Graustufenbild erzeugt
			Image greyscale = new GreyscaleImage();
			greyscale.convertImage(getImg().getWidth(), getImg().getHeight(), getArgb(), 0);
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case "DILATATION":
			BufferedImage dilatationImage = new BufferedImage(getImg().getWidth() + 4, getImg().getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
			int[] dilatArray = new int[dilatationImage.getHeight() * dilatationImage.getWidth()];
			MorphOperations dilatation = new Dilatation();
			dilatation.prepareMorphOperation(dilatationImage, dilatArray, getArgb(), getImg().getWidth(), getImg().getHeight());
			dilatation.executeMorpOperation(dilatationImage.getWidth(), dilatationImage.getHeight(), dilatArray, getArgb(), getImg().getWidth(), getHalfKernelSize(), getKernel());			
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case "EROSION":
			//Erosion erhät man wenn man auf ein Binärbild eine inversion durchführt, darauf dann eine Dilatation und
			//zum Schluss nochmal eine Inversion
			invertImage(img.getWidth(), img.getHeight(), getArgb());
			BufferedImage erosionImage = new BufferedImage(getImg().getWidth() + 4, getImg().getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
			int[] erosionArray = new int[erosionImage.getHeight() * erosionImage.getWidth()];
			MorphOperations dilatation1 = new Dilatation();
			dilatation1.prepareMorphOperation(erosionImage, erosionArray, getArgb(), getImg().getWidth(), getImg().getHeight());
			dilatation1.executeMorpOperation(erosionImage.getWidth(), erosionImage.getHeight(), erosionArray, getArgb(), getImg().getWidth(), getHalfKernelSize(), getKernel());			
			invertImage(img.getWidth(), img.getHeight(), getArgb());
			drawImage(getImg().getHeight(), getImg().getWidth(), getArgb());
			break;
		case "GAUSS":
			//Um das Bild zu gläten
			Filter gauss = new GaussFilter();
			gauss.filterOperation(getImg().getWidth(), getImg().getHeight(), getHalfKernelSize(), getGridSize(), getArgb(), getUpdatedArray());
			drawImage(getImg().getHeight(), getImg().getWidth(), getUpdatedArray());
			break;
		case "EDGE":
			//
			Filter laPlace = new LaPlaceFilter();
			laPlace.filterOperation(getImg().getWidth(), getImg().getHeight(), getSizeBorder(), getGridSize(), getArgb(), getUpdatedArray());
			drawImage(getImg().getHeight(), getImg().getWidth(), getUpdatedArray());
			break;
		
		case "CONTOUR":
			//Diese Contourverfolgung funktioniert nur bei Anwendung auf ein Binärbild
			Contour c = new Contour(getImg().getWidth(), getImg().getHeight(), getArgb());
			c.twoPass();
			c.pavlidisAlgo();
			if(c.compareSizeOfArea()){
				drawImage(getImg().getHeight(), getImg().getWidth(),c.getResultContour());
			}
			break;

		case "NOOPERATION":
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
	
	public void invertImage(int width, int height, int[] argb){
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int pos = y*width+x;
				if((argb[pos]&0xff)==255)
					argb[pos] = 0x00000000; 
				else
					argb[pos] = 0xffffffff;
			}
		}
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
		Scene scene = new Scene(addBorderPane());		
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
		getComboBox().getItems().add("Select");
		getComboBox().getItems().add("Grayscale");
		getComboBox().getItems().add("Binary"); 
		getComboBox().getItems().add("Edge"); 
		getComboBox().getItems().add("Gauss"); 
		getComboBox().getItems().add("Dilatation"); 
		getComboBox().getItems().add("Erosion"); 
		getComboBox().getItems().add("Contour");
		getComboBox().getSelectionModel().select(0);
		getComboBox().valueProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String old, String selected) {
				//Bei Auswahl eines Items aus der ComboBox 
				selectOperation(selected);
			}    
		});
		getradioBtn3().setText("3er Matrix");
		getradioBtn5().setText("5er Matrix");
		tGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	           @Override
	           public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	               if (tGroup.getSelectedToggle() != null) {
	                   RadioButton button = (RadioButton) gettGroup().getSelectedToggle();
	                   if(button.getText().contains("3er Matrix")){
	                	   setSizeBorder(1);
	                	//   cleanGrid();
	                	   //fillGridWithText(button.getText(), getChoiceOperation());
	                   }else{
	                	   setSizeBorder(2);
	                	 //  cleanGrid();
	                	   //fillGridWithText(button.getText(),  getChoiceOperation());
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
	private void selectOperation(String selected){
		if(selected.contains("Edge")){
			updateViewEdge();
			setChoiceOperation("EDGE");
		}
		else if(selected.contains("Gauss")){
			updateViewForNonSelectableOperations();
			setChoiceOperation("GAUSS");
			//fillGridWithText("5er Matrix", getChoiceOperation());
			
		}
		else if(selected.contains("Dilatation")){
			updateViewMorph();
			drawHist=false;
			createCenter();
			setChoiceOperation("DILATATION");
		}else if(selected.contains("Erosion")){
			updateViewMorph();
			drawHist=false;
			
			setChoiceOperation("EROSION");
		}
		else if(selected.contains("Binary")){
			updateViewForNonSelectableOperations();
			drawHist=false;
			createCenter();
			setChoiceOperation("BINARY");
		}
		else if(selected.contains("Grayscale")){
			updateViewForNonSelectableOperations();
			drawHist=true;
			createCenter();
			setChoiceOperation("GRAYSCALE");
		}
		else if(selected.contains("Contour")){
			updateViewForNonSelectableOperations();
			setChoiceOperation("CONTOUR");
		}
		else if(selected.contains("Select")){
			System.out.println("Select");
			updateViewEdge();
			setChoiceOperation("NOOPERATION");
		}
		
	}
	
	//GUI update wenn Dilatation bzw Erosion ausgewaehlt wird
	@SuppressWarnings("restriction")
	private void updateViewMorph(){
		getvBoxLeftBorder().setDisable(false);
		getradioBtn3().setDisable(true);
		getradioBtn5().setDisable(true);
		getradioBtn3().setSelected(true);
		setSizeBorder(1);
		//cleanGrid();
	}
	
	//GUI update wenn Graustufenbild, Binärbild oder GaussFilter angewendet wird
	@SuppressWarnings("restriction")
	private void updateViewForNonSelectableOperations(){
		getradioBtn3().setDisable(true);
		getradioBtn5().setDisable(true);
		getvBoxLeftBorder().setDisable(true);
		getradioBtn3().setSelected(true);
		setSizeBorder(1);
		//cleanGrid();
	}
	
	@SuppressWarnings("restriction")
	private void updateViewEdge(){
		getvBoxLeftBorder().setDisable(true);
		getradioBtn3().setDisable(false);
		getradioBtn5().setDisable(false);
		//cleanGrid();
		
	}
	
	
	
	
	@SuppressWarnings("restriction")
	private void createCenter(){
		
		paneCenter = makePaneCenter(getGridSize());
		//Pane t = drawHistogram();
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
	
	
	//
	@SuppressWarnings("restriction")
	private void createLeftBorder(){
		getBtn1().setText("Button 1");
		getBtn1().setOnAction(actionEvent -> {
			double radius = 1.0;
			setKernel(this.createKernel(radius));
			this.fillGridKernel(kernel);
		});
		getBtn2().setText("Button 2");
		getBtn2().setOnAction(actionEvent -> {
			double radius = 1.5;
			setKernel(this.createKernel(radius));
			this.fillGridKernel(kernel);
		});
		getBtn3().setText("Button 3");
		getBtn3().setOnAction(actionEvent -> {
			double radius = 2.0;
			setKernel(this.createKernel(radius));
			this.fillGridKernel(kernel);
		});
		getBtn4().setText("Button 4");
		getBtn4().setOnAction(actionEvent -> {
			double radius = 2.7;
			setKernel(this.createKernel(radius));
			this.fillGridKernel(getKernel());
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
	
	
	//Hier wird das Grid erstellt
	@SuppressWarnings("restriction")
	public Pane makePaneCenter(int size) {
		Pane pane = new Pane();
		if(drawHist){
			fillRGBWithValues();
			LineChart<String, Number> histo= drawHistogram();
			pane.getChildren().add(histo);
			System.out.println("Betritt draw Histo");
		}
		else{
			bearb.addListener(new ChangeListener() {
			    @Override
			    public void changed(ObservableValue o, Object oldVal, Object newVal) {
			        System.out.println(bearb);
			    }
			}); 
		
			
			adjustGrid(getPaneCenter().getWidth(), 400, size, pane);
			pane.widthProperty().addListener(e->{
				adjustGrid(getPaneCenter().getWidth(), getPaneCenter().getHeight(), size, pane);
				System.out.println("Betritt width property");
				});
			
			
		}
		return pane;
	}
	
	
	
	




	private void adjustGrid(double widthPane, double heightPane, int size, Pane pane){
		double width = widthPane / size;
		System.out.println("Widthhhhhhhh" + width);
		double height = heightPane/size;
		pane.getChildren().clear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				getTextForGrid()[i][j] = new Text("i");
				getRec()[i][j] = new Rectangle();
				getRec()[i][j].setX(i * width);
				getRec()[i][j].setY(j * height);
				getRec()[i][j].setWidth(width);
				getRec()[i][j].setHeight(height);
				getRec()[i][j].setFill(null);
				getRec()[i][j].setStroke(Color.BLACK);
				System.out.println("Bin in adjustGrid");
				pane.getChildren().addAll(getRec()[i][j],getTextForGrid()[i][j]);}}
	}
	
	
	//Bei Dilatation und Erosion wird der Kernel auf das Grid gezeichnet 
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
	
	
	//Alle Operationen die Grid in der Gui mit Zahlen befüllen
//	@SuppressWarnings("restriction")
//	private void fillGridWithText(String nameMatrix, String nameOperation){
//		//cleanGrid();
//		double width = getWidthOfGrid() / getGridSize();
//		for (int i = 0; i < getGridSize(); i++) {
//			for (int j = 0; j < getGridSize(); j++) {
//				getTextForGrid()[i][j].setX(i *width+17);
//				getTextForGrid()[i][j].setY(j*width+30);
//				getTextForGrid()[i][j].setFont(Font.font ("Verdana", 20));
//				if(nameMatrix.contains("3er Matrix")&&nameOperation.contains("EDGE"))
//					getTextForGrid()[i][j].setText(getlPF3Matrix()[i][j]);
//				else if (nameMatrix.contains("5er Matrix")&&nameOperation.contains("EDGE"))
//					getTextForGrid()[i][j].setText(getlPF5Matrix()[i][j]);
//				else if (nameMatrix.contains("5er Matrix")&&nameOperation.contains("GAUSS")){
//					getTextForGrid()[i][j].setText(getGaussMatrix5x5()[i][j]);
//				}					
//			}
//		}
//	}
	
	//Das Grid in der GUI wird gelöscht bzw alle Zahlen und Farben gelöscht
	//@SuppressWarnings("restriction")
//	private void cleanGrid(){
//		for (int i = 0; i < getGridSize(); i++) {
//			for (int j = 0; j < getGridSize(); j++) {
//				getRec()[i][j].setFill(Color.WHITE);
//				getTextForGrid()[i][j].setText("");
//			}
//		}
//	}
	
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
	}
	


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

		for(int i=0; i<256;i++){
			seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
			seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
			seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
		}

		chartHistogram.getData().addAll(seriesRed, seriesGreen, seriesBlue);
		return chartHistogram;
	}
	
	
	private void fillRGBWithValues(){
		for (int i = 0; i < 256; i++) {
			alpha[i] = red[i] = green[i] = blue[i] = 0;
		}
		for(int y =0; y< getImg().getHeight(); y++){
			for(int x=0;x<getImg().getWidth(); x++){
				int pos = y*getImg().getWidth()+x;
				int r = argb[pos]>>16&0xff;
				int g = argb[pos]>>8&0xff;
				int b = argb[pos]&0xff;
				red[r]++;
				green[g]++;
				blue[b]++;
			}
		}
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
	
	public BooleanProperty getBearb() {
		return bearb;
	}




	public void setBearb(BooleanProperty bearb) {
		this.bearb = bearb;
	}
	
}
