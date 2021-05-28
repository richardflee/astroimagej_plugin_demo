package rfl.astroimagej.dev.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CatalogUtilsTest {	
	
	@DisplayName("Confirm path to vspdemo.properties file")	
	@Test
	void testPath_To_PropertiesFile() {
		
		String filePath = "C:\\Users\\rlee1\\.astroimagej\\vspdemo.properties";
		System.out.println(filePath);
		System.out.println(CatalogUtils.getPropsPath());
		assertEquals(filePath, CatalogUtils.getPropsPath());	
	}

}
