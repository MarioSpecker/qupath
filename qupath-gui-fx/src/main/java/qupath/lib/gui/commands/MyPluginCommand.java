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
//import java.awt.Color;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.ByteArrayInputStream;

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

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
		this.gridSize = 5;
		this.kernel = new boolean[gridSize][gridSize];
		this.rec = new Rectangle[gridSize][gridSize];

	}

	@SuppressWarnings("restriction")
	@Override
	public void run() {

		dialog = createDialog();
		dialog.showAndWait();
		

		BufferedImage img = qupath.getViewer().getThumbnail();			//create Image
		argb = new int[img.getHeight() * img.getWidth()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), argb, 0, img.getWidth());
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), argb, 0, img.getWidth());
		toGrayScale(img.getHeight(), img.getWidth(), argb);
		int threshold = getIterativeThreshold(argb, img.getWidth(), img.getHeight());
		binarize(argb, img.getWidth(), img.getHeight(), threshold);
		
		BufferedImage resizedImage = new BufferedImage(img.getWidth()+4, img.getHeight()+4, BufferedImage.TYPE_INT_ARGB);
		resizedImage.createGraphics().setColor(java.awt.Color.white);
		resizedImage.setRGB(2, 2, img.getWidth(), img.getHeight(), argb, 0, img.getWidth());
		resizedARGB = new int[resizedImage.getHeight()*resizedImage.getWidth()];	
		resizedImage.getRGB(0, 0, resizedImage.getWidth(), resizedImage.getHeight(), resizedARGB, 0, resizedImage.getWidth());
		dilatation(resizedImage.getWidth(), resizedImage.getHeight(), resizedARGB, argb);
		drawImage(img.getHeight(), img.getWidth(), argb);
		//drawImage(resizedImage.getHeight(), resizedImage.getWidth(), argb);
//		img.getRGB(0, 0, img.getWidth(), img.getHeight(), argb, 0, img.getWidth());
//		toGrayScale(img.getHeight(), img.getWidth(), argb);
//		int threshold = getIterativeThreshold(argb, img.getWidth(), img.getHeight());
//		binarize(argb, img.getWidth(), img.getHeight(), threshold);
//		drawImage(img.getHeight(), img.getWidth(), argb);
	}
	

	private void drawImage(int height, int width, int[] rgb) {
		qupath.getViewer().getThumbnail()
				.setRGB(0, 0, width, height, rgb, 0, width);
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

	private void toGrayScale(int height, int width, int [] rgb) {
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
	
	

	private void dilatation(int width, int height, int[] resizedArray, int[] rgb){
		for(int y=2;y<height-2;y++){
			for(int x=2;x<width-2;x++){
				int pos = y*width+x;
				int pixelCenter = resizedArray[pos] & 0xff;
				if(pixelCenter==255){
					for(int j=-getHalfKernelSize(); j<=getHalfKernelSize();j++){
						for(int i=-getHalfKernelSize(); i<=getHalfKernelSize();i++){
							if(kernel[i+getHalfKernelSize()][j+getHalfKernelSize()]==true){
								int pix = resizedArray[(y-j)*width+(x-i)]& 0xff;
								if(pix==0){
									int black = 0x000000;
									rgb[(y-getHalfKernelSize())*(width-4)+(x-getHalfKernelSize())] = (0xFF<<24) | (black<<16) | (black<<8) | black;
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
	
	private int getHalfKernelSize(){
		return 2;
	}

	// Graphic....

	@SuppressWarnings("restriction")
	protected Stage createDialog() {
		Stage dialog = new Stage();
		dialog.initOwner(qupath.getStage());
		dialog.setTitle("My Plugin Dialog");

		dialog.setScene(new Scene(addBorderPane(), 400, 400));
		return dialog;
	}

	@SuppressWarnings("restriction")
	private BorderPane addBorderPane() {
		BorderPane root = new BorderPane();

		Pane b = makeGrid(gridSize);
		root.setAlignment(b, Pos.CENTER);
		root.setMargin(b, new Insets(20, 20, 20, 20));
		root.setCenter(b);
		HBox vb = new HBox();
		Button btn1 = new Button();
		btn1.setText("Button 1");
		btn1.setOnAction(actionEvent -> {
			double radius = 1.0;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);
			// this.printKernel(kernel(radius));
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
		Button btn4 = new Button();
		btn4.setText("Button 4");
		btn4.setOnAction(actionEvent -> {
			double radius = 2.7;
			kernel = this.createKernel(radius);
			this.fillGridKernel(kernel);
			// this.printKernel(kernel);
		});
		vb.setHgrow(btn1, Priority.ALWAYS);
		vb.setHgrow(btn2, Priority.ALWAYS);
		vb.setHgrow(btn3, Priority.ALWAYS);
		vb.setHgrow(btn4, Priority.ALWAYS);
		btn1.setMaxWidth(Double.MAX_VALUE);
		btn2.setMaxWidth(Double.MAX_VALUE);
		btn3.setMaxWidth(Double.MAX_VALUE);
		btn4.setMaxWidth(Double.MAX_VALUE);
		vb.getChildren().addAll(btn1, btn2, btn3, btn4);

		root.setBottom(vb);

		return root;

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
