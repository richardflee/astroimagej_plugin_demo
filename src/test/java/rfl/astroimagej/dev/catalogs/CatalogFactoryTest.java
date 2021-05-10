package rfl.astroimagej.dev.catalogs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import rfl.astroimagej.dev.enums.CatalogMagType;
import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.FieldObject;
import rfl.astroimagej.dev.queries.QueryResult;

class CatalogFactoryTest {

	private final double epsilon = 1.0e-5;
	
//	// SIMBAD vega query copied from SimbadCatalogTest
//	@DisplayName("Confirm that a SIMBAD vega query runs when selected in CatalogFactory")
//	@Test
//	void CatalogFactory_SimbadVegaQuery_ReturnsVegaResult() throws Exception {
//		// configure factory to run simbad vega query
//		AstroCatalog catalog = CatalogFactory.createCatalog(CatalogMagType.SIMBAD);
//		CatalogQuery query = new CatalogQuery();
//		query.setObjectId("vega");
//		QueryResult result = catalog.runQuery(query);
//
//		// delete leading '*' returned in simbad vega name & match objectId
//		String rx = "[^a-zA-Z0-9\\s\\w\\-\\.]";
//		String s1 = result.getSimbadId().replaceAll(rx, "").trim();
//		String s2 = "alf Lyr".replaceAll(rx, "").trim();
//		assertEquals(s2, s1);
//
//		// Ra & Dec
//		assertEquals(18.61565, result.getSimbadRaHr(), epsilon);
//		assertEquals(38.78369, result.getSimbadDecDeg(), epsilon);
//
//		// mag B, V, Rc, Ic data
//		assertEquals(0.03, result.getMagB(), epsilon);
//		assertEquals(0.03, result.getMagV(), epsilon);
//		assertEquals(0.07, result.getMagR(), epsilon);
//		assertEquals(0.10, result.getMagI(), epsilon);
//	}

	// VSP vega query copied from VspCatalogTest
	@DisplayName("Confirm that a VSP vega query runs when selected in CatalogFactory")
	@Test
	void CatalogFactory_VspVegaQuery_ReturnsVegaResult() throws Exception {
		// configure factory to run vsp vega query
		AstroCatalog catalog = CatalogFactory.createCatalog(CatalogMagType.VSP);
		CatalogQuery vegaQuery = new CatalogQuery();
		vegaQuery.setObjectId("vega");
		vegaQuery.setRaHr(18.61565);
		vegaQuery.setDecDeg(38.78369);
		vegaQuery.setFovAmin(60.0);
		vegaQuery.setMagLimit(16.0);
		vegaQuery.setCatalogType(CatalogMagType.VSP);
		vegaQuery.setMagBand("B");		
		
	
		QueryResult result = catalog.runQuery(vegaQuery);		
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

}
