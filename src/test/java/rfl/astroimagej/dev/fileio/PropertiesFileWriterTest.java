package rfl.astroimagej.dev.fileio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.CatalogType;

class PropertiesFileWriterTest {
	
	private static PropertiesFileWriter fw;
	private static CatalogQuery query0;
	
	private final double epsilon = 1.0e-5;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		fw = new PropertiesFileWriter();
	}

	@DisplayName("Import default values from .properties file into query0")
	@BeforeEach
	void setUp() throws Exception {
		query0 = new CatalogQuery();
		fw.writePropertiesFile(query0);
	}
	
	@DisplayName("Save default values in .properties file")
	@AfterEach
	void tearDown() throws Exception {
		fw.writePropertiesFile(query0);
	}
	
	@DisplayName("Test FormData object is unchanged after write/read cycle")
	@Test
	void testProperties_ReadWrite_EndToEnd() {
		CatalogQuery query1 = PropertiesFileReader.readPropertiesFile();
		assertTrue(query1.getObjectId().equals(query0.getObjectId()));
		
		assertEquals(query1.getRaHr(), query0.getRaHr(), epsilon);
		assertEquals(query1.getDecDeg(), query0.getDecDeg(), epsilon);
		assertEquals(query1.getFovAmin(), query0.getFovAmin(), epsilon);
		assertEquals(query1.getMagLimit(), query0.getMagLimit(), epsilon);
		
		assertTrue(query1.getCatalogType().equals(query0.getCatalogType()));
		assertTrue(query1.getMagBand().equals(query0.getMagBand()));
	}
	
	@DisplayName("Test modified FormData object is equal after write/read cycle")
	@Test
	void testProperties_AfterModification_ReadWrite_EndToEnd() {
		// compile a new data set
		CatalogQuery query1 = new CatalogQuery();		
		query1.setObjectId("WISP-120");		
		query1.setRaHr(0.5 * 6.50911);
		query1.setDecDeg(0.5 * 29.67230);		
		query1.setFovAmin(0.5 * 60.0);
		query1.setMagLimit(0.5 * 15.0);		
		query1.setCatalogType(CatalogType.APASS);
		query1.setMagBand("SR");
		
		// test: write fData1 & read fData2
		fw.writePropertiesFile(query1);
		CatalogQuery query2 = PropertiesFileReader.readPropertiesFile();
		
		// *** confirm rewrite/read fData2
		// match strings
		assertTrue(query2.getObjectId().equals(query1.getObjectId()));
		assertTrue(query2.getMagBand().equals(query1.getMagBand()));
		
		// match catalog
		assertTrue(query2.getCatalogType().equals(query1.getCatalogType()));
		
		// match double within epsilon (5 places)
		assertEquals(query2.getRaHr(), query1.getRaHr(), epsilon);
		assertEquals(query2.getDecDeg(), query1.getDecDeg(), epsilon);
		assertEquals(query2.getFovAmin(), query1.getFovAmin(), epsilon);
		assertEquals(query2.getMagLimit(), query1.getMagLimit(), epsilon);
		assertEquals(query2.getMagLimit(), query1.getMagLimit(), epsilon);
		
		// *** confirm changes from fData0
		// match strings
		assertFalse(query2.getObjectId().equals(query0.getObjectId()));
		assertFalse(query2.getMagBand().equals(query0.getMagBand()));
		
		// match catalog
		assertFalse(query2.getCatalogType().equals(query0.getCatalogType()));
		
		// match double within epsilon (5 places)
		assertEquals(query2.getRaHr(), 0.5 * query0.getRaHr(), epsilon);
		assertEquals(query2.getDecDeg(), 0.5 * query0.getDecDeg(), epsilon);
		assertEquals(query2.getFovAmin(), 0.5 * query0.getFovAmin(), epsilon);
		assertEquals(query2.getMagLimit(), 0.5 * query0.getMagLimit(), epsilon);
		assertEquals(query2.getMagLimit(), 0.5 * query0.getMagLimit(), epsilon);
	}
}
