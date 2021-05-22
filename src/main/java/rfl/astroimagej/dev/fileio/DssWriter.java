package rfl.astroimagej.dev.fileio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.CatalogType;
import rfl.astroimagej.dev.utils.CatalogUrls;

/**
 * Queries the SkyView server to download a Digitized Sky Survey (DSS) fits file
 * for the user-specified coordinates and field of view. 
 * <p>
 * Fit (and radec) files are saved in the ..astroimagej/radec folder, with format: 
 * [objectid].[magband].[fov_amin].fits
 * </p>
 * <p>
 * The user opens the DSS fits image in AstroImageJ and imports apertures defined in the radec file.
 * AstroImageJ draws photometry apertures over images of the target and listed comparison stars.
 * </p>
 * <p>
 * SkyView Ref: https://skyview.gsfc.nasa.gov/current/docs/batchpage.html
 * </p> 
 */
public class DssWriter {

	/**
	 * Compile fits filename based on catalog query data. If this is a new file in destination
	 * folder then runs a SkyView server query on DSS catalog to download 1000x1000 fits 
	 * image for the specified sky region. 
	 * 
	 * @param query sky coordinate and field-of-view data
	 * 
	 * @return message whether successful in writing DSS fits file
	 */
	public static String downloadDssFits(CatalogQuery query) {
		// compile DSS url for query parameters
		String skyUrl = CatalogUrls.getUrl(query, CatalogType.DSS);
		
		// connect a File variable with the compiled fits filename & get file path
		File file = RaDecFileWriter.getFile(query, "fits");
		String filePath = file.toString();
		
		// if fits file does not already exist
		// attempts to download & save a new dss fits file
		String message = String.format("Fit file: %s already exists", filePath);
		if (!file.exists()) {
			try {
				InputStream in = new URL(skyUrl).openStream();
				Files.copy(in, Paths.get(filePath));
				message = String.format("Saved fits file: %s", filePath);
			} catch (IOException e) {
				message = String.format("Error in writing file: %s", filePath);
			}
		} 
		return message;
	}
}
