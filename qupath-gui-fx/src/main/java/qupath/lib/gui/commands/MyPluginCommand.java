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
	private VBox vb;
	private BorderPane root;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btnOk;
	private ComboBox<String> comboBox;
	private RadioButton rBtn3; 
	private RadioButton rBtn5;
	private ToggleGroup tGroup;
	private VBox vBox;
	
	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
		this.gridSize = 5;
		this.kernel = new boolean[gridSize][gridSize];
		this.rec = new Rectangle[gridSize][gridSize];

	}

	@SuppressWarnings("restriction")
	@Override
	public void run() {
		initNodes();
		
		
		
		dialog = createDialog();
		dialog.showAndWait();

		BufferedImage img = qupath.getViewer().getThumbnail(); // create Image
		argb = new int[img.getHeight() * img.getWidth()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), argb, 0, img.getWidth());
		
		toGrayScale(img.getHeight(), img.getWidth(), argb);
		int threshold = getIterativeThreshold(argb, img.getWidth(), img.getHeight());
		binarize(argb, img.getWidth(), img.getHeight(), threshold);
		//drawImage(img.getHeight(), img.getWidth(), argb);
		
		BufferedImage dilatationImage = new BufferedImage(img.getWidth() + 4, img.getHeight() + 4, BufferedImage.TYPE_INT_ARGB);
		int[] dilatArray = new int[dilatationImage.getHeight() * dilatationImage.getWidth()];
		prepareImageForDilatation(dilatationImage, dilatArray, argb, img.getWidth(), img.getHeight());
		dilatation(dilatationImage.getWidth(), dilatationImage.getHeight(), dilatArray, argb, img.getWidth());
		drawImage(img.getHeight(), img.getWidth(), argb);

		
		
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

	@SuppressWarnings("unused")
	private void binarize(int[] rgb, int height, int width, int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int pix = argb[pos] & 0xff;

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
							if (kernel[i + getHalfKernelSize()][j + getHalfKernelSize()] == true) {
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

	// Graphic....

	@SuppressWarnings("restriction")
	protected Stage createDialog() {
		Stage dialog = new Stage();
		dialog.initOwner(qupath.getStage());
		dialog.setTitle("My Plugin Dialog");

		dialog.setScene(new Scene(addBorderPane(), 500, 500));
		return dialog;
	}

	@SuppressWarnings("restriction")
	private BorderPane addBorderPane() {



		

		
		center();
		leftBorder();
		rightBorder();

		return root;

	}
	
	
	@SuppressWarnings("restriction")
	private void rightBorder(){
		
		comboBox.getItems().add("Morph");
		comboBox.getItems().add("Edge"); 
		comboBox.getSelectionModel().select(0);
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String old, String selected) {
				if(selected.contains("Morph")){
					
				}
				else if(selected.contains("Edge")){
					vb.setDisable(true);


				}
				else{

				}

			}    
		});
		//rBtn3.setText("3er Matrix");
		//rBtn5.setText("5er Matrix");
		tGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	           @Override
	           public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	               // Has selection.
	               if (tGroup.getSelectedToggle() != null) {
	                   RadioButton button = (RadioButton) tGroup.getSelectedToggle();
	                   System.out.println("Button: " + button.getText());
	                   
	               }
	           }
	       });
		
		rBtn3.setToggleGroup(tGroup);
		rBtn5.setToggleGroup(tGroup);
		vBox.getChildren().addAll(comboBox,rBtn3,rBtn5);
		root.setRight(vBox);
	}
	
	@SuppressWarnings("restriction")
	private void center(){
		Pane b = makeGrid(gridSize);
		root.setAlignment(b, Pos.CENTER);
		root.setMargin(b, new Insets(20, 20, 20, 20));
		root.setCenter(b);
	}
	
	
	@SuppressWarnings("restriction")
	private void leftBorder(){
		
		btn1.setText("Button 1");
		btn1.setOnAction(actionEvent -> {
			double radius = 1.0;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);
		});
		Button btn2 = new Button();
		btn2.setText("Button 2");
		btn2.setOnAction(actionEvent -> {
			double radius = 1.5;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);

		});
		Button btn3 = new Button();
		btn3.setText("Button 3");
		btn3.setOnAction(actionEvent -> {
			double radius = 2.0;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);

		});
		btn4.setText("Button 4");
		btn4.setOnAction(actionEvent -> {
			double radius = 2.7;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);
		});
		vb.setVgrow(btn1, Priority.ALWAYS);
		vb.setVgrow(btn2, Priority.ALWAYS);
		vb.setVgrow(btn3, Priority.ALWAYS);
		vb.setVgrow(btn4, Priority.ALWAYS);
		btn1.setMaxHeight(Double.MAX_VALUE);
		btn2.setMaxHeight(Double.MAX_VALUE);
		btn3.setMaxHeight(Double.MAX_VALUE);
		btn4.setMaxHeight(Double.MAX_VALUE);
		vb.getChildren().addAll(btn1, btn2, btn3, btn4);
		root.setLeft(vb);
	}
	
	

	@SuppressWarnings("restriction")
	public static Pane makeGrid(int size) {
		double width = 300 / size;
		Pane p = new Pane();
		// rec = new Rectangle[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				rec[i][j] = new Rectangle();
				rec[i][j].setX(i * width);
				rec[i][j].setY(j * width);
				rec[i][j].setWidth(width);
				rec[i][j].setHeight(width);
				rec[i][j].setFill(null);
				rec[i][j].setStroke(Color.BLACK);
				p.getChildren().add(rec[i][j]);
			}
		}
		return p;
	}
	
	@SuppressWarnings("restriction")
	private void initNodes(){
		vb = new VBox();
		root = new BorderPane();
		btn1 = new Button();
		btn2 = new Button();
		btn3 = new Button();
		btn4 = new Button();
		btnOk = new Button();
		comboBox = new ComboBox<String>();
		rBtn3 = new RadioButton();
		rBtn5 = new RadioButton();
		vBox = new VBox();
		tGroup = new ToggleGroup();
	}

	private void fillGridKernel(boolean[][] kernel) {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (kernel[i][j] == true) {
					rec[i][j].setFill(Color.RED);
				} else {
					rec[i][j].setFill(Color.WHITE);
				}
			}

		}
	}
}
