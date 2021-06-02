package rfl.astroimagej.dev._plugin;

import java.awt.EventQueue;

import javax.swing.UIManager;

import ij.plugin.PlugIn;
import rfl.astroimagej.dev.catalog_ui.CatalogFormUI;
import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.fileio.PropertiesFileReader;
import rfl.astroimagej.dev.fileio.PropertiesFileWriter;
import rfl.astroimagej.dev.fileio.RaDecFileWriter;

/**
 * AstroImageJ plugin to import user-specified VSP (Variable Star Plotter) data and
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
	
	private final static String VERSION_NUMBER = "WIN10-1.00";
	
	/**
	 * ImageJ calls the run method when user selects AstroImageJ/Plugin/Vsp_Demo menu option
	 */
	@Override
	public void run(String arg) {
		main(null);
	}
	
	/**
	 * Runs as a Java app, invoked from astroimagej plugin
	 */
	public static void main(String[] args) {
		/**
		 * Replaces Swing Metal with Sets system look-and-feel
		 * Win10 => "Windows" (tested)
		 * Linux & Solaris => "GTK+" if GTK+ 2.2 or later installed, "Motif" otherwise
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize Windows Look-Feel");
		}
		
		// runs swing app in event dispatching thread (EDT)
		// implemented as singled thread app
		EventQueue.invokeLater(() -> {
			runVspDemo();
		});
	}
	
	/**
	 * Main app - sets up file writer event listeners and opens catalog dialog window 
	 */
	public static void runVspDemo() {
		
		// Read last saved catalog dialog data from properties file
		// First run loads default WASP-12 data set
		CatalogQuery query = PropertiesFileReader.readPropertiesFile();
		
		// Instantiates file writer objects
		PropertiesFileWriter pfw = new PropertiesFileWriter();
		RaDecFileWriter rdw = new RaDecFileWriter();
		
		// Opens catalog user interface with properties or default data  
		CatalogFormUI catalogUi = new CatalogFormUI(query);
		
		// sets up file writer as listeners to catalog query & save property file events
		catalogUi.setPropsWriterListener(pfw);
		catalogUi.setRaDecWriterListener(rdw);
		
		// finally set JDialog modal and visible after objects and form complete initialisation
		// Set dialog modal to lock-out AIJ toolbar while vsp_demo open, otherwise bad things can happen ..
		catalogUi.setModal(true);
		catalogUi.setVisible(true);
	}
	
	/**
	 * Sets app version number as text
	 * 
	 * @return current version in format 'vA.BC'
	 */
	public static String getVersion() {
		String version = VERSION_NUMBER;
		return version;
	}
}
