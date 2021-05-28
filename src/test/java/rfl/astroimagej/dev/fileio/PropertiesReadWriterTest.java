package rfl.astroimagej.dev.fileio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import rfl.astroimagej.dev.enums.CatalogType;
import rfl.astroimagej.dev.queries.CatalogQuery;

/*
 * Tests confirm that data is unchanged after .properties file write / read cycle 
 */

class PropertiesReadWriterTest {

	private static PropertiesReadWriter rw;
	private static CatalogQuery fData0;
	private final double epsilon = 1.0e-5;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		rw = new PropertiesReadWriter();
	}


	@DisplayName("Import default values from .properties file into base fData0")
	@BeforeEach
	void setUp() throws Exception {
		fData0 = new CatalogQuery();
		rw.writeCatalogUiProperties(fData0);
	}

	@DisplayName("Save default values in .properties file")
	@AfterEach
	void tearDown() throws Exception {
		rw.writeCatalogUiProperties(fData0);
	}

	@DisplayName("Test FormData object is unchanged after write/read cycle")
	@Test
	void testProperties_ReadWrite_EndToEnd() {
		CatalogQuery fData1 = rw.readCatalogUiProperties();
		assertTrue(fData1.getObjectId().equals(fData0.getObjectId()));
		
		assertEquals(fData1.getRaHr(), fData0.getRaHr(), epsilon);
		assertEquals(fData1.getDecDeg(), fData0.getDecDeg(), epsilon);
		assertEquals(fData1.getFovAmin(), fData0.getFovAmin(), epsilon);
		assertEquals(fData1.getMagLimit(), fData0.getMagLimit(), epsilon);
		
		assertTrue(fData1.getCatalogType().equals(fData0.getCatalogType()));
		assertTrue(fData1.getMagBand().equals(fData0.getMagBand()));
	}
	
	@DisplayName("Test modified FormData object is equal after write/read cycle")
	@Test
	void testProperties_AfterModification_ReadWrite_EndToEnd() {
		// compile a new data set
		CatalogQuery fData1 = new CatalogQuery();		
		fData1.setObjectId("WISP-120");		
		fData1.setRaHr(0.5 * 6.50911);
		fData1.setDecDeg(0.5 * 29.67230);		
		fData1.setFovAmin(0.5 * 60.0);
		fData1.setMagLimit(0.5 * 15.0);		
		fData1.setCatalogType(CatalogType.APASS);
		fData1.setMagBand("SR");
		
		// test: write fData1 & read fData2
		rw.writeCatalogUiProperties(fData1);
		CatalogQuery fData2 = rw.readCatalogUiProperties();
		
		// *** confirm rwrite/read fData2
		// match strings
		assertTrue(fData2.getObjectId().equals(fData1.getObjectId()));
		assertTrue(fData2.getMagBand().equals(fData1.getMagBand()));
		
		// match catalog
		assertTrue(fData2.getCatalogType().equals(fData1.getCatalogType()));
		
		// match double within epsilon (5 places)
		assertEquals(fData2.getRaHr(), fData1.getRaHr(), epsilon);
		assertEquals(fData2.getDecDeg(), fData1.getDecDeg(), epsilon);
		assertEquals(fData2.getFovAmin(), fData1.getFovAmin(), epsilon);
		assertEquals(fData2.getMagLimit(), fData1.getMagLimit(), epsilon);
		assertEquals(fData2.getMagLimit(), fData1.getMagLimit(), epsilon);
		
		// *** confirm changes from fData0
		// match strings
		assertFalse(fData2.getObjectId().equals(fData0.getObjectId()));
		assertFalse(fData2.getMagBand().equals(fData0.getMagBand()));
		
		// match catalog
		assertFalse(fData2.getCatalogType().equals(fData0.getCatalogType()));
		
		// match double within epsilon (5 places)
		assertEquals(fData2.getRaHr(), 0.5 * fData0.getRaHr(), epsilon);
		assertEquals(fData2.getDecDeg(), 0.5 * fData0.getDecDeg(), epsilon);
		assertEquals(fData2.getFovAmin(), 0.5 * fData0.getFovAmin(), epsilon);
		assertEquals(fData2.getMagLimit(), 0.5 * fData0.getMagLimit(), epsilon);
		assertEquals(fData2.getMagLimit(), 0.5 * fData0.getMagLimit(), epsilon);
	}
}
