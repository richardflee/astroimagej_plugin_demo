package rfl.astroimagej.dev.catalog_simbad;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimbadCatalogTest {
	private SimbadCatalog simbad;
	
	private final double epsilon = 1.0e-5;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		simbad = new SimbadCatalog();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@DisplayName("Default WASP-12 data matches SIMBAD query")
	@Test
	void defaultData_MatchSimbadQuery_IsCorrect() throws SimbadNotFoundException {
		String inputTargetName = "wasp12";
		
		SimbadQueryData simbadData = simbad.runQuery(inputTargetName);	
		// simbadID
		assertEquals("WASP-12", simbadData.getIdentifier());		
		
		//Ra & Dec
		assertEquals(6.50911, simbadData.getRaHr(), epsilon);
		assertEquals(29.67230, simbadData.getDecDeg(), epsilon);
		
		// mag B, V data
		assertEquals(12.14, simbadData.getMagB(), epsilon);
		assertEquals(11.57, simbadData.getMagV(), epsilon);
		
		// mag Rc, Ic no data 
		assertNull(simbadData.getMagRc());
		assertNull(simbadData.getMagIc());		
	}
	
	@DisplayName("Vega data matches SIMBAD query")
	@Test
	void vegaData_MatchSimbadQuery_IsCorrect() throws SimbadNotFoundException {
		String inputTargetName = "vega";
		
		SimbadQueryData simbadData = simbad.runQuery(inputTargetName);	
		// simbadID
		String rx = "[^a-zA-Z0-9\\s\\w\\-\\.]";
		
		String s1 = simbadData.getIdentifier().replaceAll(rx,  "").trim();
		String s2 = "alf Lyr".replaceAll(rx,  "").trim();		
		assertEquals(s2, s1);		
		
		//Ra & Dec
		assertEquals(18.61565, simbadData.getRaHr(), epsilon);
		assertEquals(38.78369, simbadData.getDecDeg(), epsilon);
		
		// mag B, V, Rc, Ic data
		assertEquals(0.03, simbadData.getMagB(), epsilon);
		assertEquals(0.03, simbadData.getMagV(), epsilon);
		assertEquals(0.07, simbadData.getMagRc(), epsilon);
		assertEquals(0.10, simbadData.getMagIc(), epsilon);
	}
	
	@DisplayName("Object identifier not matched in SIMBAD database throws SimbadNotFoundException")
	@Test
	void whenSimbadNotFoundExceptionThrown_thenAssertionSucceeds() {
		String inputTargetName = "WISP-12";
	    assertThrows(SimbadNotFoundException.class, () -> {
	    	simbad.runQuery(inputTargetName);
	    });
	}
}
