package qupath.lib.gui.commands;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.helpers.DisplayHelpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;








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
import java.io.ByteArrayInputStream;

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
	private int[] arrayLaPlace;
	private int[][] laPlaceFilter;
	private VBox vBoxRightBorder;
	private BorderPane root;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btnOk;
	private ComboBox<String> comboBox;
	private RadioButton radioBtn3; 
	private RadioButton radioBtn5;
	private ToggleGroup tGroup;
	private VBox vBox;
	private HBox hBox;
	private BufferedImage img;
	private int threshold;
	private static Text[][] textForGrid;
	private static int widthOfGrid;
	private static String[][] lPF3Matrix;
	private static String[][] lPF5Matrix;
	

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
		this.gridSize = 5;
		this.kernel = new boolean[gridSize][gridSize];
		this.rec = new Rectangle[gridSize][gridSize];
		this.textForGrid = new Text[gridSize][gridSize];
		this.widthOfGrid = 250;
		this.lPF3Matrix = new String[][]{{"0","0","0","0","0"},{"0","0","-1","0","0"},{"0","-1","4","-1","0"},{"0","0","-1","0","0"},{"0","0","0","0","0"}};
		this.lPF5Matrix = new String[][]{{"0","0","-1","0","0"},{"0","-1","-2","-1","0"},{"-1","-2","16","-2","-1"},{"0","-1","-2","-1","0"},{"0","0","-1","0","0"}};
		
	}

	



	@SuppressWarnings("restriction")
	@Override
	public void run() {
		initNodes();
		
		img = qupath.getViewer().getThumbnail(); // create Image
		argb = new int[img.getHeight() * img.getWidth()];
		arrayLaPlace = new int[img.getHeight() * img.getWidth()];
		
		getImg().getRGB(0, 0, getImg().getWidth(), getImg().getHeight(), getArgb(), 0, getImg().getWidth());
		toGrayScale(getImg().getHeight(), getImg().getWidth(), argb);
		System.arraycopy(getArgb(), 0, getArrayLaPlace(), 0, getArgb().length);
		setThreshold(getIterativeThreshold(getArgb(), getImg().getWidth(), getImg().getHeight()));
		
		dialog = createDialog();
		dialog.showAndWait();

		
		drawImage(getImg().getHeight(),getImg().getWidth(), getArrayLaPlace());
		
		
		
		
		
		
		//binarize(argb, img.getWidth(), img.getHeight(), threshold);
		//drawImage(img.getHeight(), img.getWidth(), argb);
		
		
		
		
//		BufferedImage dilatationImage = new BufferedImage(img.getWidth() + 4, img.getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
//		int[] dilatArray = new int[dilatationImage.getHeight() * dilatationImage.getWidth()];
//		prepareImageForDilatation(dilatationImage, dilatArray, argb, img.getWidth(), img.getHeight());
//		dilatation(dilatationImage.getWidth(), dilatationImage.getHeight(), dilatArray, argb, img.getWidth());
//		drawImage(img.getHeight(), img.getWidth(), argb);

		
		
//		BufferedImage erosionImage = new BufferedImage(img.getWidth() + 4, img.getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
//		int[] erosionArray = new int[erosionImage.getHeight() * erosionImage.getWidth()];
//		prepareImageForErosion(erosionImage, erosionArray, argb, img.getWidth(), img.getHeight());
//		
//		erosion(erosionImage.getWidth(), erosionImage.getHeight(), erosionArray, argb, img.getWidth());
//		drawImage(img.getHeight(), img.getWidth(), argb);
		
		
		
	}

	private void drawImage(int height, int width, int[] rgb) {
		qupath.getViewer().getThumbnail().setRGB(0, 0, width, height, rgb, 0, width);
		qupath.getViewer().repaintEntireImage();
	}

	
	private void binarize(int[] rgb, int height, int width, int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int pix = rgb[pos] & 0xff;

				if (pix < threshold) {
					pix = 0x00000000;
				} else {
					pix = 0xffffffff;
				}
				rgb[pos] = (0xFF << 24) | (pix << 16) | (pix << 8) | pix;
			}
		}
	}

	private void toGrayScale(int height, int width, int[] rgb) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int r = (rgb[pos] >> 16) & 0xff;
				int g = (rgb[pos] >> 8) & 0xff;
				int b = rgb[pos] & 0xff;
				int avg = (r + g + b) / 3;
				rgb[pos] = ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
			}
		}
	}

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
		boolean[][] k = new boolean[gridSize][gridSize];
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
	
	
		
	private void prepareImageForDilatation(BufferedImage dilatImage, int[] dilatArray, int[] argbArray, int widthDefaultImage, int heightDefaultImage) {		
		dilatImage.createGraphics().setColor(java.awt.Color.WHITE);
		dilatImage.createGraphics().fillRect(0, 0, dilatImage.getWidth(), dilatImage.getHeight());
		dilatImage.setRGB(2, 2, widthDefaultImage, heightDefaultImage, argbArray, 0, widthDefaultImage);
		dilatImage.getRGB(0, 0, dilatImage.getWidth(), dilatImage.getHeight(), dilatArray, 0, dilatImage.getWidth());
	}
	
	private void prepareImageForErosion(BufferedImage erosionImage, int[] erosionArray, int[] argbArray, int widthDefaultImage, int heightDefaultImage) {
		erosionImage.createGraphics().setColor(java.awt.Color.BLACK);
		erosionImage.createGraphics().fillRect(0, 0, erosionImage.getWidth(), erosionImage.getHeight());
		erosionImage.setRGB(2, 2, widthDefaultImage, heightDefaultImage, argbArray, 0, widthDefaultImage);
		erosionImage.getRGB(0, 0, erosionImage.getWidth(), erosionImage.getHeight(), erosionArray, 0, erosionImage.getWidth());
		
	}

	private void dilatation(int width, int height, int[] resizedArray, int[] rgb, int defaultImageWidth) {
		for (int y = 2; y < height - 2; y++) {
			for (int x = 2; x < width - 2; x++) {
				int pos = y * width + x;
				int pixelCenter = resizedArray[pos] & 0xff;
				if (pixelCenter == 255) {
					for (int j = -getHalfKernelSize(); j <= getHalfKernelSize(); j++) {
						for (int i = -getHalfKernelSize(); i <= getHalfKernelSize(); i++) {
							if (getKernel()[i + getHalfKernelSize()][j + getHalfKernelSize()] == true) {
								int pix = resizedArray[(y - j) * width+ (x - i)] & 0xff;
								if (pix == 0) {
									int black = 0x000000;
									int position = (y - getHalfKernelSize()) * (defaultImageWidth) + (x - getHalfKernelSize());
									rgb[position] = (0xFF << 24)
											| (black << 16)
											| (black << 8)
											| black;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	

	private void erosion(int width, int height, int[] resizedArray, int[] rgb, int defaultImageWidth) {
		for (int y = 2; y < height - 2; y++) {
			for (int x = 2; x < width - 2; x++) {
				int pos = y * width + x;
				int pixelCenter = resizedArray[pos] & 0xff;
				if (pixelCenter == 0) {
					for (int j = -getHalfKernelSize(); j <= getHalfKernelSize(); j++) {
						for (int i = -getHalfKernelSize(); i <= getHalfKernelSize(); i++) {
							if (kernel[i + getHalfKernelSize()][j + getHalfKernelSize()] == true) {
								int pix = resizedArray[(y - j) * width+ (x - i)] & 0xff;
								if(pix == 255){
									int white = 0xffffff;
									rgb[(y - getHalfKernelSize()) * (defaultImageWidth) + (x - getHalfKernelSize())] = (0xFF << 24)
											| (white << 16)
											| (white << 8)
											| white;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	public void printKernel(boolean[][] kernel) {
		for (boolean[] xS : kernel) {
			for (boolean v : xS) {

				System.out.println(v);
			}
			System.out.println();
		}

	}

	private int getHalfKernelSize() {
		return 2;
	}

	private void edgeDetection(int width, int height, int sizeBorder, int[] argb, int[] arrayLP, int threshold){
		for (int y = sizeBorder; y < height-sizeBorder; y++) {
			for (int x = sizeBorder; x<width -sizeBorder; x++) { 
				int pixel;
				if(sizeBorder==1)
				pixel = getPixelFromLP3(x, y, width, argb);
				else
				pixel = getPixelFromLP5(x, y, width, argb);	
				
				
				if(pixel<0)pixel=0;
				else if(pixel>255)pixel=255;
				arrayLP[y*width+x] = ((0xFF << 24) | (pixel << 16) | (pixel << 8) | pixel);
			}
		}
	}
	
	
	private int getPixelFromLP3(int x, int y, int width, int[] argb){
		int pix2 = argb[(y-1)*width+x]&0xff;
		int pix4 = argb[y*width+(x-1)]&0xff;
		int pix5 = argb[y*width+x]&0xff;
		int pix6 = argb[(y)*width+(x+1)]&0xff;
		int pix8 = argb[(y+1)*width+(x)]&0xff;
		int pixel = (-pix2 - pix4 + 4*pix5 -pix6 - pix8);
		return pixel;
	}
	
	private int getPixelFromLP5(int x, int y, int width, int[] argb){
		int pix02 = argb[(y-2)*width+x]&0xff;
		int pix11 = argb[(y-1)*width+(x-1)]&0xff;
		int pix12 = argb[(y-1)*width+x]&0xff;
		int pix13 = argb[(y-1)*width+(x+1)]&0xff;
		int pix20 = argb[y*width+(x-2)]&0xff;
		int pix21 = argb[y*width+(x-1)]&0xff;
		int pix22 = argb[y*width+x]&0xff;
		int pix23 = argb[y*width+(x+1)]&0xff;
		int pix24 = argb[y*width+(x+2)]&0xff;
		int pix31 = argb[(y+1)*width+(x-1)]&0xff;
		int pix32 = argb[(y+1)*width+x]&0xff;
		int pix33 = argb[(y+1)*width+(x+1)]&0xff;
		int pix42 = argb[(y+2)*width+x]&0xff;
		int pixel = (-pix02 - pix11 -(2*pix12)- pix13 -pix20 - (2*pix21) +(16*pix22) -(2*pix23)
				- pix24 - pix31 -(2+pix32) -pix33 - pix42);
		return pixel;
	}
	
	
	private void gaussFilter(int width, int height, int sizeBorder, int gridSize, int[] argb){
		int pixel;
		for (int y = sizeBorder; y < height-sizeBorder; y++) {
			for (int x = sizeBorder; x<width -sizeBorder; x++) {
				
				if(sizeBorder==1)
					pixel = getPixelFromGauss3Matrix(x, y, sizeBorder, argb);
				else
					pixel = getPixelFromGauss5Matrix(x, y, width, gridSize, sizeBorder,  argb) ;
			}
		}
		if(pixel<0)pixel=0;
		else if(pixel>255)pixel=255;
		argb[y*width+x] = ((0xFF << 24) | (pixel << 16) | (pixel << 8) | pixel);
	}
	
	private int getPixelFromGauss3Matrix(int x, int y, int width, int[] argb){
		int pix00 = argb[(y-1)*width+(x-1)]&0xff;
		int pix01 = argb[(y-1)*width+(x)]&0xff;
		int pix02 = argb[(y-1)*width+(x+1)]&0xff;
		int pix10 = argb[(y)*width+(x-1)]&0xff;
		int pix11 = argb[(y)*width+(x)]&0xff;
		int pix12 = argb[(y)*width+(x+1)]&0xff;
		int pix20 = argb[(y+1)*width+(x-1)]&0xff;
		int pix21 = argb[(y+1)*width+(x)]&0xff;
		int pix22 = argb[(y+1)*width+(x+1)]&0xff;
		int pixel = (1/16*pix00)+(2/16*pix01)+(1/16*pix02)+(2/16*pix10)+(4/16*pix11)+(2/16*pix12)+(1/16*pix20)+(2/16*pix21)+(1/16*pix22);
		return pixel;
	}
	
	private int getPixelFromGauss5Matrix(int x, int y, int width, int gridSize, int sizeBorder, int[] argb){
		int [][] a= new int[gridSize][gridSize];
		for (int j = -sizeBorder; j < sizeBorder; j++) {
			for (int i = -sizeBorder; i<sizeBorder; i++) {
				a[i+2][j+2] = argb[(y-j)*width+(x-i)]&0xff;	
			}
		}
		int pixel = (1/273*a[0][0])+(4/273*a[0][1])+(7/273*a[0][2])+(4/273*a[0][3])+(1/273*a[0][4])
				+(4/273*a[1][0])+(16/273*a[1][1])+(26/273*a[1][2])+(16/273*a[1][3])+(4/273*a[1][4])
				+(7/273*a[2][0])+(26/273*a[2][1])+(41/273*a[2][2])+(26/273*a[2][3])+(7/273*a[2][4])
				+(4/273*a[3][0])+(16/273*a[3][1])+(26/273*a[3][2])+(16/273*a[3][3])+(4/273*a[3][4])
				+(1/273*a[4][0])+(4/273*a[4][1])+(7/273*a[4][2])+(4/273*a[4][3])+(1/273*a[4][4]);
		
		return pixel;
	}
	
	
	
	
	
	
	// ++++++++++++++++++++++++++++++++++++++Graphic....+++++++++++++++++++++++++++++++++++++++++

	

	@SuppressWarnings("restriction")
	protected Stage createDialog() {
		Stage dialog = new Stage();
		dialog.initOwner(qupath.getStage());
		dialog.setTitle("My Plugin Dialog");

		dialog.setScene(new Scene(addBorderPane(), 500, 350));
		return dialog;
	}

	@SuppressWarnings("restriction")
	private BorderPane addBorderPane() {
		createCenter();
		createLeftBorder();
		createRightBorder();
		createBottom();
		return root;

	}
	
	
	@SuppressWarnings("restriction")
	private void createRightBorder(){
		getvBox().setPadding(new Insets(10,10,10,10));
		getComboBox().getItems().add("Morph");
		getComboBox().getItems().add("Edge"); 
		getComboBox().getSelectionModel().select(0);
		getComboBox().valueProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String old, String selected) {
				if(selected.contains("Morph")){
					getvBoxRightBorder().setDisable(false);
				}
				else if(selected.contains("Edge")){
					getvBoxRightBorder().setDisable(true);
				}
				else{

				}
			}    
		});
		getradioBtn3().setText("3er Matrix");
		getradioBtn5().setText("5er Matrix");
		tGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	           @Override
	           public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	               if (tGroup.getSelectedToggle() != null) {
	            	   
	                   RadioButton button = (RadioButton) tGroup.getSelectedToggle();
	                   System.out.println("Button: " + button.getText());
	                   int sizeBorder=0;
	                   if(button.getText().contains("3er Matrix")){
	                	   sizeBorder=1;
	                	   cleanGrid();
	                	   fillGridWithText(button.getText());
	                	   edgeDetection(getImg().getWidth(), getImg().getHeight(), sizeBorder , getArgb(), getArrayLaPlace(), getThreshold());
	                   }else{
	                	   sizeBorder=2;
	                	   cleanGrid();
	                	   fillGridWithText(button.getText());
	                	   edgeDetection(getImg().getWidth(), getImg().getHeight(), sizeBorder , getArgb(), getArrayLaPlace(), getThreshold());
	                   }
	               }
	           }
	       });
		getradioBtn3().setToggleGroup(tGroup);
		getradioBtn5().setToggleGroup(tGroup);
		vBox.getChildren().addAll(comboBox,getradioBtn3(),getradioBtn5());
		root.setRight(vBox);
	}
	
	
	
	
	@SuppressWarnings("restriction")
	private void createCenter(){
		Pane b = makeGrid(getGridSize());
		root.setAlignment(b, Pos.CENTER);
		root.setMargin(b, new Insets(20, 20, 20, 20));
		root.setCenter(b);
	}
	
	
	@SuppressWarnings("restriction")
	private void createBottom(){
		btnOk.setText("OK");
		btnOk.setOnAction(actionEvent -> {
			dialog.close();
		});
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(10,10,10,10));
		hBox.setHgrow(btnOk, Priority.ALWAYS);
		btnOk.setMaxWidth(Double.MAX_VALUE);
		hBox.getChildren().add(btnOk);
		root.setBottom(hBox);
	}
	
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
		getvBoxRightBorder().setVgrow(btn1, Priority.ALWAYS);
		getvBoxRightBorder().setVgrow(btn2, Priority.ALWAYS);
		getvBoxRightBorder().setVgrow(btn3, Priority.ALWAYS);
		getvBoxRightBorder().setVgrow(btn4, Priority.ALWAYS);
		getBtn1().setMaxHeight(Double.MAX_VALUE);
		getBtn2().setMaxHeight(Double.MAX_VALUE);
		getBtn3().setMaxHeight(Double.MAX_VALUE);
		getBtn4().setMaxHeight(Double.MAX_VALUE);
		getvBoxRightBorder().getChildren().addAll(getBtn1(), getBtn2(),getBtn3(), getBtn4());
		getvBoxRightBorder().setPadding(new Insets(10,10,10,10));
		root.setLeft(getvBoxRightBorder());
	}
	
	
	
	

	@SuppressWarnings("restriction")
	public static Pane makeGrid(int size) {
		double width = getWidthOfGrid() / size;
		Pane p = new Pane();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				getTextForGrid()[i][j] = new Text();
				getRec()[i][j] = new Rectangle();
				getRec()[i][j].setX(i * width);
				getRec()[i][j].setY(j * width);
				getRec()[i][j].setWidth(width);
				getRec()[i][j].setHeight(width);
				getRec()[i][j].setFill(null);
				getRec()[i][j].setStroke(Color.BLACK);
				p.getChildren().addAll(getRec()[i][j],getTextForGrid()[i][j]);
			}
		}
		return p;
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
	
	@SuppressWarnings("restriction")
	private void fillGridWithText(String name){
		double width = getWidthOfGrid() / getGridSize();
		for (int i = 0; i < getGridSize(); i++) {
			for (int j = 0; j < getGridSize(); j++) {
				getTextForGrid()[i][j].setX(i * width+30);
				getTextForGrid()[i][j].setY(j * width+30);
				switch(name){
				case "3er Matrix":
					cleanGrid();
					getTextForGrid()[i][j].setText(getlPF3Matrix()[i][j]);
					break;
				case "5er Matrik":
					cleanGrid();
					getTextForGrid()[i][j].setText(getlPF5Matrix()[i][j]);
					break;
				}
			}
		}
	}
	
	private void cleanGrid(){
		for (int i = 0; i < getGridSize(); i++) {
			for (int j = 0; j < getGridSize(); j++) {
				getRec()[i][j].setFill(Color.WHITE);
				getTextForGrid()[i][j].setText("");
			}
		}
	}
	
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
		vBox = new VBox(15);
		tGroup = new ToggleGroup();
		hBox = new HBox();
		
	}

	
	
	
	

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

	public int[] getArrayLaPlace() {
		return arrayLaPlace;
	}

	public void setArrayLaPlace(int[] arrayLaPlace) {
		this.arrayLaPlace = arrayLaPlace;
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

	

	public VBox getvBoxRightBorder() {
		return vBoxRightBorder;
	}





	public void setvBoxRightBorder(VBox vBoxRightBorder) {
		this.vBoxRightBorder = vBoxRightBorder;
	}










	public BorderPane getRoot() {
		return root;
	}

	public void setRoot(BorderPane root) {
		this.root = root;
	}

	public Button getBtn1() {
		return btn1;
	}

	public void setBtn1(Button btn1) {
		this.btn1 = btn1;
	}

	public Button getBtn2() {
		return btn2;
	}

	public void setBtn2(Button btn2) {
		this.btn2 = btn2;
	}

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

	public VBox getvBox() {
		return vBox;
	}

	public void setvBox(VBox vBox) {
		this.vBox = vBox;
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
	
	public RadioButton getRadioBtn3() {
		return radioBtn3;
	}

	public void setRadioBtn3(RadioButton radioBtn3) {
		this.radioBtn3 = radioBtn3;
	}

	public RadioButton getRadioBtn5() {
		return radioBtn5;
	}

	public void setRadioBtn5(RadioButton radioBtn5) {
		this.radioBtn5 = radioBtn5;
	}
	
	public static Text[][] getTextForGrid() {
		return textForGrid;
	}

	public static void setTextForGrid(Text[][] textForGrid) {
		MyPluginCommand.textForGrid = textForGrid;
	}
	
	public static int getWidthOfGrid() {
		return widthOfGrid;
	}


	public void setWidthOfGrid(int widthOfGrid) {
		this.widthOfGrid = widthOfGrid;
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
	
}
