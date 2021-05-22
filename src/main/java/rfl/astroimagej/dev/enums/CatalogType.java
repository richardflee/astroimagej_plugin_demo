package rfl.astroimagej.dev.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enum list of on-line astronomical catalogs
 * <p> Internally encodes catalog magnitude bands as a '.' delimited string.
 * Method magBands decodes string and returns string array of filter / magnitude names for selected catalog
 * </p>
 */
public enum CatalogType {
	SIMBAD("B.V.R.I"), 
	VSP("B.V.Rc.Ic"), 
	APASS("B.V.SR.SG"), 
	DSS("");
	
	private String magBand;	
	
	CatalogType (String magBand) {
		this.magBand = magBand;
	}
	
	/**
	 * Decodes '.' delimited filters list into array
	 * 
	 * @return list of catalog filters / magnitude bands
	 */
	public List<String> getMagBands() {
		return Arrays.asList(magBand.split("\\."));
	}
}
