package rfl.astroimagej.dev.vsp_demo;

import java.awt.EventQueue;

import javax.swing.UIManager;

import ij.plugin.PlugIn;
import rfl.astroimagej.dev.catalog_ui.CatalogUI;
import rfl.astroimagej.dev.catalog_ui.PropertiesReadWriter;

/**
 * Demonstrates AstroImageJ plugin to import user-specified VSP (Variable Star Plotter) data and
 * save to an AIJ-compatible radec format file.
 * <p>
 * Note: class name (Vsp_Dem) <b>must</b> include '_' character to appear as an option under AIJ Plugins menu.
 * </p>
 * <p>
 * Refer ImageJ documentation 'Developing Plugins for ImageJ 1.x 
 * https://imagej.net/Developing_Plugins_for_ImageJ_1.x 
 * </p>
 *
 */
public class Vsp_Demo implements PlugIn  {

	@Override
	public void run(String arg) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize Windows Look-Feel");
		}
		
		// configure requestHandler for catalog download user selection
		EventQueue.invokeLater(() -> {
			new CatalogUI(new PropertiesReadWriter());
		});
	}
}
