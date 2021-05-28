package rfl.astroimagej.dev.catalog_ui;

import rfl.astroimagej.dev.enums.CatalogFilters;
import rfl.astroimagej.dev.query_base.AstroObject;

/**
 * Data set specifying astronomical catalog download request parameters
 * <p>
 * Parameters are saved to vspdemo.properties file in user.home/.astroimagej directory
 * </p>
 */

public class CatalogFormData extends AstroObject {
	private CatalogFilters astroCatalog;
//	private String targetName;
//	private Double raHr;
//	private Double decDeg;
	private Double fovAmin;
	private Double magLimit;
	private String filterBand;
	
	/**
	 * Default constructor used to create a new vspdemo.properties file
	 */
	public CatalogFormData() {
		super();
		this.astroCatalog = CatalogFilters.VSP;
//		this.targetName = "WASP12";
//		this.raHr = 6.50911;
//		this.decDeg = 29.67230;
		this.fovAmin = 60.0;
		this.magLimit = 17.0;
		this.filterBand = "Rc";		
	}

	/**
	 * Encapsulates on-line catalog query parameters
	 * 
	 * @param astroCatalog on-line photometry catalog, currently VSP is the only implemented option
	 * @param targetName object name, optionally in VSP catalog
	 * @param raHr J2000 Ra in hours (0 - 24 hr)
	 * @param decDeg J2000 Dec in degrees (±90 deg)
	 * @param fovAmin field-of-view in arcmin
	 * @param magLimit maximum object magnitude to download
	 * @param filterBand selected photometric filter
	 */
	public CatalogFormData(CatalogFilters astroCatalog, String targetName, Double raHr, Double decDeg, Double fovAmin,
			Double magLimit, String filterBand) {
		this.astroCatalog = astroCatalog;
		super.setIdentifier(targetName);
		super.setRaHr(raHr);
		super.setDecDeg(decDeg);
		this.fovAmin = fovAmin;
		this.magLimit = magLimit;
		this.filterBand = filterBand;
	}

	public CatalogFilters getAstroCatalog() {
		return astroCatalog;
	}

	public void setAstroCatalog(CatalogFilters astroCatalog) {
		this.astroCatalog = astroCatalog;
	}

	public Double getFovAmin() {
		return fovAmin;
	}

	public void setFovAmin(Double fovAmin) {
		this.fovAmin = fovAmin;
	}

	public Double getMagLimit() {
		return magLimit;
	}

	public void setMagLimit(Double magLimit) {
		this.magLimit = magLimit;
	}

	public String getFilterBand() {
		return filterBand;
	}

	public void setFilterBand(String filterBand) {
		this.filterBand = filterBand;
	}


	
	
}
