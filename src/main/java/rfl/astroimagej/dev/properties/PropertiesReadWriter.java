package rfl.astroimagej.dev.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.enums.CatalogMagType;
import rfl.astroimagej.dev.queries.CatalogQuery;

public class PropertiesReadWriter {

	/**
	 * Writes form settings to vspdemo.properties in user.home/.astroimagej folder
	 * <p>
	 * * The list of available vsp filters is fixed. To add a new filter to the
	 * list, edit the properties file to include new filter option. E.g. to add
	 * filter X to the end of the list, append '.X' to the vspfilters property
	 * (vspfilters=V.B.Rc.Ic.X)
	 * </p>
	 * 
	 * @param event event object encapsulating form data inputs
	 */
	public void writeVspProperties(CatalogQuery data) {		
		// update properties file with FormData values
		// display error dialog in case of file write error
		// CatalogQuery cq = data;
		try (OutputStream output = new FileOutputStream(PropertiesReadWriter.getPropsPath())) {
			Properties prop = new Properties();
			
			// on-line catalog selected
			prop.setProperty("catalog", data.getCatalogType().toString());
			
			// copy FormData field to corresponding chart_ui text fields
			prop.setProperty("target", data.getObjectId());
			
			prop.setProperty("ra", String.format("%.5f", data.getRaHr()));
			prop.setProperty("dec",  String.format("%.5f", data.getDecDeg()));
			prop.setProperty("fov", String.format("%.1f", data.getFovAmin()));			
			prop.setProperty("magLimit", String.format("%.1f", data.getMagLimit()));
			prop.setProperty("filter", data.getMagBand());

			prop.store(output, null);
		} catch (IOException io) {
			String msg = "Failed to update properties file: \n" + PropertiesReadWriter.getPropsPath();
			JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Imports user form settings in vspdemo.properties in user.home/.astroimagej
	 * 
	 * @return CatalogRequest event encapsulating chartUI field values
	 */
	public CatalogQuery readVspProperties() {
		// if necessary, create a new, properties file with default target data for WASP-12
		File file = new File(PropertiesReadWriter.getPropsPath());
		if (!file.exists()) {
			createVspFile();
		}
		
		// import properties data and return encapsulated data in FormData object
		CatalogQuery data = new CatalogQuery();
		try (InputStream input = new FileInputStream(PropertiesReadWriter.getPropsPath())) {
			Properties prop = new Properties();
			prop.load(input);
			
			// copy field values to corresponding FormData fields
			// target name
			data.setObjectId(prop.getProperty("target").toString());
			
			// ra in hr
			data.setRaHr(Double.parseDouble(prop.getProperty("ra").toString()));
			
			// dec in deg
			data.setDecDeg(Double.parseDouble(prop.getProperty("dec").toString()));
			
			// fov in amin
			data.setFovAmin(Double.parseDouble(prop.getProperty("fov").toString()));	
			
			// mag limit
			data.setMagLimit(Double.parseDouble(prop.getProperty("magLimit").toString()));
			
			// selected catalog VSP, APASS ..
			data.setCatalogType(CatalogMagType.valueOf(prop.getProperty("catalog")));
			
			// selected filter in selected catalog
			data.setMagBand(prop.getProperty("filter").toString());
			
		} catch (IOException ex) {
			String msg = "Failed to read properties file: \n" + PropertiesReadWriter.getPropsPath();
			JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
		}
		return data;
	}
	
	/**
	 * Returns path to properties file user.home/.astroimagej/vspdemo.properties
	 * 
	 * @return path properties file path 
	 */
	public static String getPropsPath() {
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		return Paths.get(homePath, ".astroimagej", "vspdemo.properties").toString();
	}

	/*
	 * Attempts to create new vspdemo.properties file. Dialog reports outcome
	 */
	private void createVspFile() {
		// create FormData object with default (WASP-12) values
		// save as ../.astroimagej/vspdemo.properties
		CatalogQuery formData = new CatalogQuery();		
		writeVspProperties(formData);
		
		String msg = "Creating new properties file: \n" + PropertiesReadWriter.getPropsPath();
		msg += "\n Loading default data for target WASP-12";		
		JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
	}
}

