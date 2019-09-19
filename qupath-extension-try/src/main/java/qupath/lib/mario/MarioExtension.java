package qupath.lib.mario;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.QuPathExtension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import qupath.lib.common.GeneralTools;
import qupath.lib.gui.QuPathGUI;


public class MarioExtension implements QuPathExtension{

	private static Logger logger = LoggerFactory.getLogger(MarioExtension.class);

	public void installExtension(QuPathGUI qupath) {




		QuPathGUI.addMenuItems(
				qupath.getMenu("Extensions>Mario", true)
				//QuPathGUI.createCommandAction(new MATLABQuPathSetupCommand(qupath), "Export MATLAB scripts")
				);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
