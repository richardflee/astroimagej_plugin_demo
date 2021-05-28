package rfl.astroimagej.dev.catalog_queries;

import rfl.astroimagej.dev.catalog_ui.CatalogFormData;
import rfl.astroimagej.dev.enums.CatalogFilters;

/**
 * Class to compile url string to request astronomical catalog data.
 * Builds user inputs in catalog dialog (object ra, dec etc) into  VSP REST api 
 * <p>
 * Current implementation is for VSP (Variable Star Plotter) only. 
 * Note: at this time, there is no on-line reference to VSP api (Apr 2021)
 * </p>
 */
public class VspUrlBuilder {

	private CatalogFormData fData;
	private String urlTemplate;
	
	/**
	 * Assemble and return url string comprising fData data for selected catalog (VSP only at this time)
	 * 
	 * @param fData user input data object
	 * 
	 * @return RESTful url to download catalog data
	 */
	public VspUrlBuilder(CatalogFormData fData) {
		this.fData = fData;
	}
	public String getUrl() {
		// selected catalog AstroCalaog.VPS or .APASS
		String url = null;
		if (fData.getAstroCatalog().equals(CatalogFilters.VSP)) {
			this.urlTemplate = "https://app.aavso.org/vsp/api/chart/?format=json";
			url = compileVspUrl();
		// not implemented
		} else if (fData.getAstroCatalog().equals(CatalogFilters.APASS)) {
			this.urlTemplate = "** apass template **";
			url = "APASS";
		}
		return url;
	}
	
	// Tack ra, dec, fov and magLimit param strings to build Vsp url
	private String compileVspUrl() {
		// fov nn.n (arcmin)
		String fovParam = "&fov=" + String.format("%.1f", fData.getFovAmin());
		
		// magLimit nn.n (mag)
		String magLimitParam = "&maglimit=" + String.format("%.1f", fData.getMagLimit());
		
		// ra nnn.nnnnn (0 to 360 deg)
		String raParam = "&ra=" + String.format("%.5f", fData.getRaHr() * 15.0);
		
		// dec nn.nnnnn (0 to ± 90 deg)
		String decParam = "&dec=" + String.format("%.5f", fData.getDecDeg());
		
		// assemble url 
		String url = urlTemplate + fovParam + magLimitParam + raParam + decParam;
		return url;
	}
}
