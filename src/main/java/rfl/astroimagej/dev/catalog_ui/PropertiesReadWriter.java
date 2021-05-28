package rfl.astroimagej.dev.catalog_ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.catalog.CatalogFilters;
import rfl.astroimagej.dev.catalog.FormData;
import rfl.astroimagej.dev.utils.CatalogUtils;

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
	public void writeVspProperties(FormData formData) {		
		// update properties file with FormData values
		// display error dialog in case of file write error
		FormData fData = formData;
		try (OutputStream output = new FileOutputStream(CatalogUtils.getPropsPath())) {
			Properties prop = new Properties();
			
			// on-line catalog selected
			prop.setProperty("catalog", fData.getAstroCatalog().toString());
			
			// copy FormData field to corresponding chart_ui text fields
			prop.setProperty("target", fData.getTargetName());
			
			prop.setProperty("ra", String.format("%.5f", fData.getRaHr()));
			prop.setProperty("dec",  String.format("%.5f", fData.getDecDeg()));
			prop.setProperty("fov", String.format("%.1f", fData.getFovAmin()));			
			prop.setProperty("magLimit", String.format("%.1f", fData.getMagLimit()));
			prop.setProperty("filter", fData.getFilterBand());

			prop.store(output, null);
		} catch (IOException io) {
			String msg = "Failed to update properties file: \n" + CatalogUtils.getPropsPath();
			// msg += "\n\n Loading default data for target WASP-12";
			JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Imports user form settings in vspdemo.properties in user.home/.astroimagej
	 * 
	 * @return CatalogRequest event encapsulating chartUI field values
	 */
	public FormData readVspProperties() {
		// if necessary, create a new, properties file with default target data for WASP-12
		File file = new File(CatalogUtils.getPropsPath());
		if (!file.exists()) {
			createVspFile();
		}
		
		// import properties data and return encapsulated data in FormData object
		FormData fData = new FormData();
		try (InputStream input = new FileInputStream(CatalogUtils.getPropsPath())) {
			Properties prop = new Properties();
			prop.load(input);
			
			// copy field values to corresponding FormData fields
			// target name
			fData.setTargetName(prop.getProperty("target").toString());
			
			// ra in hr
			fData.setRaHr(Double.parseDouble(prop.getProperty("ra").toString()));
			
			// dec in deg
			fData.setDecDeg(Double.parseDouble(prop.getProperty("dec").toString()));
			
			// fov in amin
			fData.setFovAmin(Double.parseDouble(prop.getProperty("fov").toString()));	
			
			// mag limit
			fData.setMagLimit(Double.parseDouble(prop.getProperty("magLimit").toString()));
			
			// selected catalog VSP, APASS ..
			fData.setAstroCatalog(CatalogFilters.valueOf(prop.getProperty("catalog")));
			
			// selected filter in selected catalog
			fData.setFilterBand(prop.getProperty("filter").toString());
			
		} catch (IOException ex) {
			String msg = "Failed to read properties file: \n" + CatalogUtils.getPropsPath();
			JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
		}
		return fData;
	}

	/*
	 * Attempts to create new vspdemo.properties file. Dialog reports outcome
	 */
	private void createVspFile() {
		// create FormData object with default (WASP-12) values
		// save as ../.astroimagej/vspdemo.properties
		FormData formData = new FormData();		
		writeVspProperties(formData);
		
		String msg = "Creating new properties file: \n" + CatalogUtils.getPropsPath();
		msg += "\n Loading default data for target WASP-12";		
		JOptionPane.showMessageDialog(null, msg, "Properties File", JOptionPane.INFORMATION_MESSAGE);
	}
}
