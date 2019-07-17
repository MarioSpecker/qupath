package qupath.lib.gui.commands;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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

/**
 * 
 * @author Mario
 *
 */
public class MyPluginCommand implements PathCommand {

	private static int gridSize = 5;
	private Stage dialog;
	private QuPathGUI qupath;
	private byte[] argb;

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
	}

	@Override
	public void run() {

		dialog = createDialog();
		dialog.show();

		BufferedImage img = qupath.getViewer().getThumbnail();
		try {
			argb = toByteArrayAutoClosable(img, "png");
		} catch (IOException e) {

			e.printStackTrace();
		}
		toGrayScale(argb);
		int threshold = getIterativeThreshold(argb, img.getHeight(),
				img.getWidth());
		System.out.println(threshold);

	}

	private static byte[] toByteArrayAutoClosable(BufferedImage image,
			String type) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(image, type, out);
			return out.toByteArray();
		}
	}

	@SuppressWarnings("unused")
	private void binarize(byte[] bytes, int height, int width, int threshold) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int pix = argb[pos];
				pix = (pix & 0x0000ff);

				if (pix < threshold) {
					pix = 0;
				} else {
					pix = 255;
				}
				bytes[pos] = (byte) pix;
			}
		}
	}

	private void toGrayScale(byte[] bytes) {

		for (int i = 0; i < bytes.length; i++) {
			int r = (bytes[i] >> 16) & 0xff;
			int g = (bytes[i] >> 8) & 0xff;
			int b = bytes[i] & 0xff;
			int avg = (r + g + b) / 3;
			bytes[i] = (byte) ((0xFF << 24) | (avg << 16) | (avg << 8) | avg);
		}
	}

	private int getIterativeThreshold(byte[] argb, int width, int height) {
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
					int pix = argb[pos];
					pix = (pix & 0x0000ff);

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
		BorderPane root = new BorderPane();

		

		// CENTER
		
		Pane b = makeGrid(gridSize);
		root.setCenter(b);
		
		HBox vb = new HBox();
		Button btn1 = new Button();
		btn1.setOnAction(actionEvent ->  {
		        
		});
		vb.getChildren().add(btn1);

		Button btn2 = new Button();
		btn2.setOnAction(actionEvent ->  {
		      
		});
		vb.getChildren().add(btn2);

		Button btn3 = new Button();
		btn3.setText("B3");
		btn3.setOnAction(actionEvent ->  {
		       
		});
		vb.getChildren().add(btn3);

		Button btn4 = new Button();
		btn4.setOnAction(actionEvent ->  {
		       
		});
		vb.getChildren().add(btn4);

		Button btn5 = new Button();
		btn5.setText("B5");
		btn5.setOnAction(actionEvent ->  {
		       
		});
		vb.getChildren().add(btn5);
		vb.setAlignment(Pos.CENTER);
		vb.setHgrow(btn1, Priority.ALWAYS);
		vb.setHgrow(btn2, Priority.ALWAYS);
		vb.setHgrow(btn3, Priority.ALWAYS);
		vb.setHgrow(btn4, Priority.ALWAYS);
		vb.setHgrow(btn5, Priority.ALWAYS);
		vb.setPrefWidth(400);
		root.setBottom(vb);
		root.setAlignment(vb, Pos.CENTER);
		return root;

	}

	@SuppressWarnings("restriction")
	public static Pane makeGrid(int size) {

		double width = 400 / size;
		Pane p = new Pane();

		Rectangle[][] rec = new Rectangle[size][size];

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

}
