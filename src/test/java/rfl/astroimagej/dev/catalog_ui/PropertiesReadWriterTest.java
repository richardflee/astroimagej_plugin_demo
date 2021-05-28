package rfl.astroimagej.dev.catalog_ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import rfl.astroimagej.dev.catalog.CatalogFilters;
import rfl.astroimagej.dev.catalog.FormData;

/*
 * Tests confirm that data is unchanged after .properties file write / read cycle 
 */

class PropertiesReadWriterTest {

	private static PropertiesReadWriter rw;
	private static FormData fData0;
	private final double epsilon = 1.0e-3;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		rw = new PropertiesReadWriter();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@DisplayName("Import default values from .properties file into base fData0")
	@BeforeEach
	void setUp() throws Exception {
		fData0 = new FormData();
		rw.writeVspProperties(fData0);
	}

	@DisplayName("Save default values in .properties file")
	@AfterEach
	void tearDown() throws Exception {
		rw.writeVspProperties(fData0);
	}

	@DisplayName("Test FormData object is unchanged after write/read cycle")
	@Test
	void testProperties_ReadWrite_EndToEnd() {
		FormData fData1 = rw.readVspProperties();
		assertTrue(fData1.getTargetName().equals(fData0.getTargetName()));
		
		assertEquals(fData1.getRaHr(), fData0.getRaHr(), epsilon);
		assertEquals(fData1.getDecDeg(), fData0.getDecDeg(), epsilon);
		assertEquals(fData1.getFovAmin(), fData0.getFovAmin(), epsilon);
		assertEquals(fData1.getMagLimit(), fData0.getMagLimit(), epsilon);
		
		assertTrue(fData1.getAstroCatalog().equals(fData0.getAstroCatalog()));
		assertTrue(fData1.getFilterBand().equals(fData0.getFilterBand()));
	}
	
	@DisplayName("Test modified FormData object is equal after write/read cycle")
	@Test
	void testProperties_AfterModification_ReadWrite_EndToEnd() {
		// compile a new data set
		FormData fData1 = new FormData();		
		fData1.setTargetName("WISP-120");		
		fData1.setRaHr(0.5 * 6.50911);
		fData1.setDecDeg(0.5 * 29.67230);		
		fData1.setFovAmin(0.5 * 60.0);
		fData1.setMagLimit(0.5 * 17.0);		
		fData1.setAstroCatalog(CatalogFilters.APASS);
		fData1.setFilterBand("SR");
		
		// test: write fData1 & read fData2
		rw.writeVspProperties(fData1);
		FormData fData2 = rw.readVspProperties();
		
		// *** confirm rwrite/read fData2
		// match strings
		assertTrue(fData2.getTargetName().equals(fData1.getTargetName()));
		assertTrue(fData2.getFilterBand().equals(fData1.getFilterBand()));
		
		// match catalog
		assertTrue(fData2.getAstroCatalog().equals(fData1.getAstroCatalog()));
		
		// match double within epsilon (5 places)
		assertEquals(fData2.getRaHr(), fData1.getRaHr(), epsilon);
		assertEquals(fData2.getDecDeg(), fData1.getDecDeg(), epsilon);
		assertEquals(fData2.getFovAmin(), fData1.getFovAmin(), epsilon);
		assertEquals(fData2.getMagLimit(), fData1.getMagLimit(), epsilon);
		assertEquals(fData2.getMagLimit(), fData1.getMagLimit(), epsilon);
		
		// *** confirm changes from fData0
		// match strings
		assertFalse(fData2.getTargetName().equals(fData0.getTargetName()));
		assertFalse(fData2.getFilterBand().equals(fData0.getFilterBand()));
		
		// match catalog
		assertFalse(fData2.getAstroCatalog().equals(fData0.getAstroCatalog()));
		
		// match double within epsilon (5 places)
		assertEquals(fData2.getRaHr(), 0.5 * fData0.getRaHr(), epsilon);
		assertEquals(fData2.getDecDeg(), 0.5 * fData0.getDecDeg(), epsilon);
		assertEquals(fData2.getFovAmin(), 0.5 * fData0.getFovAmin(), epsilon);
		assertEquals(fData2.getMagLimit(), 0.5 * fData0.getMagLimit(), epsilon);
		assertEquals(fData2.getMagLimit(), 0.5 * fData0.getMagLimit(), epsilon);
	}
}
