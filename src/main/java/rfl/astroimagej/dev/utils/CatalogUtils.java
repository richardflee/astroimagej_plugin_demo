package rfl.astroimagej.dev.utils;

import java.nio.file.Paths;

/**
 * Class utility methods to support importing astro catalog data *
 */
public class CatalogUtils {
	
	/**
	 * Returns path to properties file user.home/.astroimagej/vspdemo.properties
	 * 
	 * @return path properties file path 
	 */
	public static String getPropsPath() {
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		return Paths.get(homePath, ".astroimagej", "vspdemo.properties").toString();
	}

}
