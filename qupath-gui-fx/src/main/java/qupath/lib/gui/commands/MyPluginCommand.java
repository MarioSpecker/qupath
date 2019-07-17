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

/**
 * 
 * @author Mario
 *
 */
public class MyPluginCommand implements PathCommand {

	private Stage dialog;
	private QuPathGUI qupath;
	private byte[] argb;

	public MyPluginCommand(final QuPathGUI qupath) {
		this.qupath = qupath;
	}

	@Override
	public void run() {

		if (dialog == null) {
			dialog = createDialog();
			dialog.show();

		}
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

		root.setPadding(new Insets(15, 20, 10, 10));

		// TOP

		Label infoLabel = new Label("-");
		infoLabel.setTextFill(Color.BLUE);
		root.setLeft(infoLabel);

		// CENTER
		Slider slider = new Slider();

		slider.setMin(0);
		slider.setMax(50);
		slider.setValue(80);

		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);

		slider.setBlockIncrement(10);

		// Adding Listener to value property.
		slider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, //
					Number oldValue, Number newValue) {

				infoLabel.setText("New value: " + newValue);
			}
		});
		root.setBottom(slider);

		// RIGHT
		Button btnRight = new Button("Right");
		btnRight.setPadding(new Insets(5, 5, 5, 5));
		root.setRight(btnRight);
		// Set margin for right area.
		BorderPane.setMargin(btnRight, new Insets(10, 10, 10, 10));

		// BOTTOM

		// Alignment.

		return root;

	}

}
