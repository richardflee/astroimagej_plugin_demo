package rfl.astroimagej.dev.catalogs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import rfl.astroimagej.dev.enums.CatalogMagType;
import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.FieldObject;
import rfl.astroimagej.dev.queries.QueryResult;

class VspCatalogTest {
	private VspCatalog vsp;
	private CatalogQuery waspQuery;
	private CatalogQuery vegaQuery;
	private QueryResult result;
	
	private final double epsilon = 1.0e-5;

	@BeforeEach
	void setUp() throws Exception {
		vsp = new VspCatalog();
		
		waspQuery = new CatalogQuery();
		
		vegaQuery = new CatalogQuery();
		vegaQuery.setObjectId("vega");
		vegaQuery.setRaHr(18.61565);
		vegaQuery.setDecDeg(38.78369);
		vegaQuery.setFovAmin(60.0);
		vegaQuery.setMagLimit(16.0);
		vegaQuery.setCatalogType(CatalogMagType.VSP);
		vegaQuery.setMagBand("B");		
	}


	@DisplayName("Vsp B-mag query Vega matches on-line photometry table")
	@Test
	void MatchVspQuery_VegaBMag_PhotometryTable_Succeeds() {
		vegaQuery.setMagBand("B");
		result = vsp.runQuery(vegaQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BCC-763")) {
				assertEquals(278.855621 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(38.392639, fo.getDecDeg(), epsilon);
				assertEquals(12.098, fo.getMag(), epsilon);
				assertEquals(0.014, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm 13 records in query and photometry table
		assertEquals(13, fieldObjects.size());
	}
	
	@DisplayName("Vsp V-mag query Vega matches on-line photometry table")
	@Test
	void matchVspQuery_VegaVMag_PhotometryTable_Succeeds() {
		vegaQuery.setMagBand("V");
		result = vsp.runQuery(vegaQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BMW-490")) {
				assertEquals(278.754241 / 15.0, fo.getRaHr(), epsilon);
				assertEquals( 38.34136, fo.getDecDeg(), epsilon);
				assertEquals(14.160, fo.getMag(), epsilon);
				assertEquals(0.009, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// match no. records in query and photometry table
		assertEquals(13, fieldObjects.size());
	}
	
	@DisplayName("Vsp Rc-mag query Vega matches on-line photometry table")
	@Test
	void matchVspQuery_VegaRcMag_PhotometryTable_Succeeds() {
		vegaQuery.setMagBand("Rc");
		result = vsp.runQuery(vegaQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BCC-827")) {
				assertEquals(279.23474121 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(38.78369522, fo.getDecDeg(), epsilon);
				assertEquals(0.049, fo.getMag(), epsilon);
				assertEquals(0.141, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// match no. records in query and photometry table
		assertEquals(13, fieldObjects.size());
	}
	
	@DisplayName("Vsp Ic-mag query Vega matches on-line photometry table")
	@Test
	void matchVspQuery_VegaIcMag_PhotometryTable_Succeeds() {
		vegaQuery.setMagBand("Ic");
		result = vsp.runQuery(vegaQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BMW-494")) {
				assertEquals(278.8600769 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(38.34899902, fo.getDecDeg(), epsilon);
				assertEquals(15.530, fo.getMag(), epsilon);
				assertEquals(0.390, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm only 12 records Ic magband
		assertEquals(12, fieldObjects.size());
	}
	
	
	@DisplayName("Vsp B-mag query wasp-12 matches on-line photometry table")
	@Test
	void matchVspQuery_WaspBMag_PhotometryTable_Succeeds() {
		waspQuery.setMagBand("B");
		result = vsp.runQuery(waspQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BKK-420")) {
				assertEquals(97.56737518 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(29.56252861, fo.getDecDeg(), epsilon);
				assertEquals(12.096, fo.getMag(), epsilon);
				assertEquals(0.099, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm 7 records B magband
		assertEquals(7, fieldObjects.size());
	}
	
	@DisplayName("Vsp V-mag query wasp-12 matches on-line photometry table")
	@Test
	void matchVspQuery_WaspVMag_PhotometryTable_Succeeds() {
		waspQuery.setMagBand("V");
		result = vsp.runQuery(waspQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BKG-165")) {
				assertEquals(97.78962708 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(29.79661179, fo.getDecDeg(), epsilon);
				assertEquals(9.747, fo.getMag(), epsilon);
				assertEquals(0.045, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm 7 records V magband
		assertEquals(7, fieldObjects.size());
	}
	
	@DisplayName("Vsp Rc-mag query wasp-12 matches on-line photometry table")
	@Test
	void matchVspQuery_WaspRcMag_PhotometryTable_Succeeds() {
		waspQuery.setMagBand("Rc");
		result = vsp.runQuery(waspQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BKG-168")) {
				assertEquals(97.78375244 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(29.69799995, fo.getDecDeg(), epsilon);
				assertEquals(12.187, fo.getMag(), epsilon);
				assertEquals(0.125, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm 5 records Rc magband
		assertEquals(5, fieldObjects.size());
	}
	
	@DisplayName("Vsp Ic-mag query wasp-12 matches on-line photometry table")
	@Test
	void matchVspQuery_WaspIcMag_PhotometryTable_Succeeds() {
		waspQuery.setMagBand("Ic");
		result = vsp.runQuery(waspQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		int count = 0;
		for (FieldObject fo : fieldObjects) {
			if (fo.getObjectId().equals("000-BKG-168")) {
				assertEquals(97.78375244 / 15.0, fo.getRaHr(), epsilon);
				assertEquals(29.69799995, fo.getDecDeg(), epsilon);
				assertEquals(11.664 , fo.getMag(), epsilon);
				assertEquals(0.156, fo.getMagErr(), epsilon);
				count = 1;
			} 		
		}
		if (count == 0) {
			Assertions.fail("Fail to match object id ");
		}
		// confirm 5 records Ic magband
		assertEquals(5, fieldObjects.size());
	}
	
	@DisplayName("Vsp query wasp-12 no results on-line photometry table")
	@Test
	void vspQuery_PhotometryTable_NoData() {
		// set fov to 1 amin => no field stars found
		waspQuery.setFovAmin(1.0);
		waspQuery.setMagBand("Ic");
		result = vsp.runQuery(waspQuery);
		List<FieldObject> fieldObjects = result.getFieldObjects();
		
		// no data returned
		assertEquals(0, fieldObjects.size());
		
	}
}
