package rfl.astroimagej.dev.fileio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import rfl.astroimagej.dev.catalogs.CatalogQuery;

/**
 * This class is an observer of FileWriter events, saving current catalog data to 
 * the properties file.
 * 
 * <p> The event fires when user clicks the Save button in the catalog dialog. </p> 
 */
public class PropertiesFileWriter implements FileWriterListener {
	
	/**
	 * Runs file write process and returns message whether operation was successful
	 */
	@Override
	public String writeFile(CatalogQuery query) {
		String message = writePropertiesFile(query);	
		return message;
	}
	
	/**
	 * Writes form settings to vspdemo.properties in user.home/.astroimagej folder
	 * 
	 * @param query encapsulates catalog form data inputs
	 * 
	 * @return message describing process success
	 */
	public String writePropertiesFile(CatalogQuery query) {		
		// updates properties file with query field values
		// returns error message in case of file write error
		String message = "";
		try (OutputStream output = new FileOutputStream(PropertiesFileReader.getPropertiesPath())) {
			Properties prop = new Properties();
			
			// selected online catalog + text field data
			prop.setProperty("catalog", query.getCatalogType().toString());
			prop.setProperty("target", query.getObjectId());			
			prop.setProperty("ra", String.format("%.5f", query.getRaHr()));
			prop.setProperty("dec",  String.format("%.5f", query.getDecDeg()));
			prop.setProperty("fov", String.format("%.1f", query.getFovAmin()));			
			prop.setProperty("magLimit", String.format("%.1f", query.getMagLimit()));
			prop.setProperty("filter", query.getMagBand());
			
			// save properties to property file
			prop.store(output, null);
			message = "Saved current dialog settings to properties file: \n" 
							+ PropertiesFileReader.getPropertiesPath();
		} catch (IOException io) {
			message = "Failed to update properties file: \n" 
							+ PropertiesFileReader.getPropertiesPath();
		}
		return message;
	}
}

