package rfl.astroimagej.dev.utils;

import java.nio.file.Paths;

public class PropertiesUtils {
	
	/**
	 * Returns path to properties file user.home/.astroimagej/vspdemo.properties
	 * 
	 * @return path full path to properties file 
	 */
	public static String getPropertiesPath() {
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		return Paths.get(homePath, ".astroimagej", "vspdemo.properties").toString();
	}

	public static void main(String[] args) {
		
		System.out.println(PropertiesUtils.getPropertiesPath());

	}

}
