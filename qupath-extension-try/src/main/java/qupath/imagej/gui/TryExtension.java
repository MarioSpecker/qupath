/*-
 * #%L
 * This file is part of QuPath.
 * %%
 * Copyright (C) 2014 - 2016 The Queen's University of Belfast, Northern Ireland
 * Contact: IP Management (ipmanagement@qub.ac.uk)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package qupath.imagej.gui;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Menus;
import ij.Prefs;
import ij.gui.ImageWindow;
import ij.gui.Overlay;
import ij.gui.Roi;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.*;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.QuPathGUI.Modes;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.imagej.detect.cells.SubcellularDetection;
import qupath.imagej.detect.dearray.TMADearrayerPluginIJ;
import qupath.imagej.detect.features.ImmuneScorerTMA;
import qupath.imagej.detect.nuclei.PositiveCellDetection;
import qupath.imagej.detect.nuclei.WatershedCellDetection;
import qupath.imagej.detect.nuclei.WatershedCellMembraneDetection;
import qupath.imagej.detect.nuclei.WatershedNucleusDetection;
import qupath.imagej.detect.tissue.PositivePixelCounterIJ;
import qupath.imagej.detect.tissue.SimpleTissueDetection;
import qupath.imagej.detect.tissue.SimpleTissueDetection2;
import qupath.imagej.helpers.IJTools;
import qupath.imagej.images.writers.TIFFWriterIJ;
import qupath.imagej.images.writers.ZipWriterIJ;
import qupath.imagej.objects.ROIConverterIJ;
import qupath.imagej.superpixels.DoGSuperpixelsPlugin;
import qupath.imagej.superpixels.SLICSuperpixelsPlugin;
import qupath.lib.analysis.objects.TileClassificationsToAnnotationsPlugin;
import qupath.lib.common.GeneralTools;
import qupath.lib.display.ImageDisplay;
import qupath.lib.gui.ImageWriterTools;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.gui.icons.PathIconFactory;
import qupath.lib.gui.prefs.PathPrefs;
import qupath.lib.gui.viewer.OverlayOptions;
import qupath.lib.images.PathImage;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.objects.PathAnnotationObject;
import qupath.lib.objects.PathCellObject;
import qupath.lib.objects.PathDetectionObject;
import qupath.lib.objects.PathObject;
import qupath.lib.objects.TMACoreObject;
import qupath.lib.objects.helpers.PathObjectColorToolsAwt;
import qupath.lib.objects.hierarchy.PathObjectHierarchy;
import qupath.lib.regions.RegionRequest;
import qupath.lib.roi.RectangleROI;
import qupath.lib.roi.interfaces.ROI;


/**
 * QuPath extension & associated static helper methods used to support integration of ImageJ with QuPath.
 * 
 * @author Pete Bankhead
 *
 */
public class TryExtension implements QuPathExtension {
	
	@Override
	public void installExtension(QuPathGUI qupath) {
		
		Menu menuRegions = qupath.getMenu("Analyze>Region identification", true);
		QuPathGUI.addMenuItems(menuRegions,
				qupath.createPluginAction("Positive pixel count (susi)", PositivePixelCounterIJ.class, null, false)
				);
		
		//Menu menuRegions = qupath.getMenu("Analyze>Region identification", true);
		// Create a new MenuItem, which shows a new script when selected
		//QuPathGUI.addMenuItems(menuRegions,
			//	qupath.createPluginAction("susi", null, null, false)
			//	);
		
		// Add to the menu
		
	}

	@Override
	public String getName() {
		return "Try Extension";
	}

	@Override
	public String getDescription() {
		return "Try to....";
	}
}