package rfl.astroimagej.dev.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.CatalogType;

/**
 * Contains PropertiesFileReader class method to import start-up properties data
 * <p> Creates a new properties file with default data on first use</p>
 */
public class PropertiesFileReader {
	
	private static final String OPTION_TITLE = "Properties File";
	
	/**
	 * Class method imports user form settings in vspdemo.properties in user.home/.astroimagej
	 * 
	 * @return CatalogQuery object encapsulating catalog dialog field values
	 */
	public static CatalogQuery readPropertiesFile() {
		// if missing, create a new, properties file with default target data for WASP-12
		File file = new File(PropertiesFileReader.getPropertiesPath());
		if (!file.exists()) {
			writeDefaultPropertiesFile();
		}
		
		// import properties data and return encapsulated data in FormData object
		CatalogQuery query = new CatalogQuery();
		try (InputStream input = new FileInputStream(PropertiesFileReader.getPropertiesPath())) {
			Properties prop = new Properties();
			prop.load(input);
			
			// copy field values to corresponding FormData fields
			// target name
			query.setObjectId(prop.getProperty("target").toString());
			
			// ra in hr
			query.setRaHr(Double.parseDouble(prop.getProperty("ra").toString()));
			
			// dec in deg
			query.setDecDeg(Double.parseDouble(prop.getProperty("dec").toString()));
			
			// fov in amin
			query.setFovAmin(Double.parseDouble(prop.getProperty("fov").toString()));	
			
			// mag limit
			query.setMagLimit(Double.parseDouble(prop.getProperty("magLimit").toString()));
			
			// selected catalog VSP, APASS ..
			query.setCatalogType(CatalogType.valueOf(prop.getProperty("catalog")));
			
			// selected filter in selected catalog
			query.setMagBand(prop.getProperty("filter").toString());
			
		} catch (IOException ex) {
			String msg = "Failed to read properties file: \n" + PropertiesFileReader.getPropertiesPath();
			JOptionPane.showMessageDialog(null, msg, OPTION_TITLE, 
					JOptionPane.INFORMATION_MESSAGE);
		}
		return query;
	}
	
	/*
	 * Attempts to create a new vspdemo.properties file. 
	 * Dialog reports outcome
	 */
	private static void writeDefaultPropertiesFile() {
		// create FormData object with default (WASP-12) values
		// save as ../.astroimagej/vspdemo.properties
		CatalogQuery query = new CatalogQuery();
		PropertiesFileWriter rw = new PropertiesFileWriter();
		rw.writePropertiesFile(query);
		
		String message = "Creating new properties file: \n" + PropertiesFileReader.getPropertiesPath();
		message += "\n Loading default data for target WASP-12";		
		JOptionPane.showMessageDialog(null, message, OPTION_TITLE, 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Returns path to properties file user.home/.astroimagej/vspdemo.properties
	 * 
	 * @return full path to properties file 
	 */
	public static String getPropertiesPath() {
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		return Paths.get(homePath, ".astroimagej", "vspdemo.properties").toString();
	}
}
