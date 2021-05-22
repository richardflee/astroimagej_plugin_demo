package rfl.astroimagej.dev.utils;

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.CatalogType;
import rfl.astroimagej.dev.enums.SimbadUrlType;

/**
 * Class methods to compiling url for on-line database queries.
 * 
 * <p> Simbad: http://simbad.u-strasbg.fr/guide/sim-url.htx </>p
 * 
 * <p> SkyView : DSS Ref: https://skyview.gsfc.nasa.gov/current/docs/batchpage.html</>p
 * 
 * <p> Vsp: Not documented (2021-05),  see https://www.aavso.org/apis-aavso-resources </>p
 */
public class CatalogUrls {

	/**
	 * Compiles a url for a Simbad database query, signature ([CatalogQuery], [SimbadUrlType]).
	 * 
	 * @param query catalog query data
	 * 
	 * @param paramType which data item to download
	 * 
	 * @return compiled Simbad url for specified paramType
	 */
	public static String getUrl(CatalogQuery query, SimbadUrlType paramType) {
		// SIMBAD header
		String url = "http://simbad.u-strasbg.fr/simbad/sim-id?output.format=votable";

		// embed user object id
		url += String.format("&Ident=%s&output.params=main_id,", query.getObjectId());

		// append url fragment for selected parameter
		url += paramType.getUrlFragment();
		return url;
	}
	
	/**
	 * Compiles a url for a database query, signature ([CatalogQuery], [CatalogType])
	 * 
	 * @param query catalog query data
	 * 
	 * @param paramType which data item to download
	 * 
	 * @return compiled url for specified catalogType
	 */
	public static String getUrl(CatalogQuery query, CatalogType catalogType) {
		String url = "";
		if (catalogType == CatalogType.DSS) {
			// SkyView header
			url += "https://skyview.gsfc.nasa.gov/cgi-bin/images?Survey=digitized+sky+survey";

			// chart centre coords = ra (deg) & dec (deg)
			url += String.format("&position=%.5f,%.5f", query.getRaHr() * 15.0, query.getDecDeg());

			// fov in deg
			url += String.format("&Size=%s", query.getFovAmin() / 60.0);

			// 1000x1000 pixels & append FITS file type
			int nPix = 1000;
			url += String.format("&Pixels=%s&Return=FITS", nPix);
			
		} else if (catalogType == CatalogType.VSP) {
			// VSP header
			url += "https://app.aavso.org/vsp/api/chart/?format=json";
			
			// fov nn.n (arcmin)
			url +=  String.format("&fov=%.1f", query.getFovAmin());
			
			// magLimit nn.n (mag)
			url += String.format("&maglimit=%.1f", query.getMagLimit()); 
			
			// ra nnn.nnnnn (0 to 360 deg)
			url += String.format("&ra=%.5f", query.getRaHr() * 15.0);
			
			// dec nn.nnnnn (0 to ± 90 deg)
			url += String.format("&dec=%.5f", query.getDecDeg());			
		}
		return url;
	}
}
