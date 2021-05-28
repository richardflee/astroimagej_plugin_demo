package rfl.astroimagej.dev.catalogs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import rfl.astroimagej.dev.enums.CatalogMagType;
import rfl.astroimagej.dev.queries.CatalogQuery;

/**
 * Queries the SkyView server to download a Digitized Sky Survey (DSS) fits file for the
 * user-specified coordinates and field of view. The fit and radec files are saved in
 *  the ..astroimagej/radec folder.
 * <p>
 *  Opening the DSS image in AstroImageJ and importing the corresponding radec file, AstroImageJ 
 *  overlays photometric apertures over target and comparison stars in the DSS image.
 * </p>
 * <p>
 * SkyView Ref: https://skyview.gsfc.nasa.gov/current/docs/batchpage.html
 * </p>
 */
public class SkyViewCatalog {
	private static final String nPixels = "1000"; 
	//private CatalogQuery query = null;
	
	/**
	 * Runs SkyView query to download 1000x1000 fits image for specified sky region.
	 * <p>
	 * Appends HHMMSS format timestamp to fits filename to prevent overwriting any fits
	 * file already open in astroimagej.
	 * </p>
	 * 
	 * @param query sky coordinate and field-of-view data
	 * @return message success or fail in writing DSS fits file
	 */
	public static String downloadDssFits(CatalogQuery query) {	
		//this.query = query;
		String skyUrl = getUrl(query);
		String filePath = getFilePath(query);
		String message = "";		
		try {
			InputStream in = new URL(skyUrl).openStream();
			Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
			message = String.format("Saved fits file: %s", filePath);
		} catch (IOException e) {
			message = String.format("Error in writing file: %s", filePath);
		}
		return message;	
	}
	
	private static String getUrl(CatalogQuery query) {
		// skyview header
		String url = "https://skyview.gsfc.nasa.gov/cgi-bin/images?Survey=digitized+sky+survey"; 
		
		// chart centre coords = ra (deg) & dec (deg) !!!!!
		url += String.format("&position=%.5f,%.5f", query.getRaHr() * 15.0, query.getDecDeg());
		
		// fov in deg
		url += String.format("&Size=%s", query.getFovAmin() / 60.0);
		
		// number pixels and append FITS file type
		url += String.format("&Pixels=%s&Return=FITS", nPixels);
		return url;
	}
	
	
	// compiles filename and path for DSS fits file, similar pattern to radec file
	// appends timestamp to filename to avoid over-writing any fits file open in aij  
	// filename format: <taget>.<magband>.<HHMMSS>.fits
	// dir format ..\AstroImageJ\radec
	private static String getFilePath(CatalogQuery query) {
		// filename
		String objectId = query.getObjectId();
		String filterBand = query.getMagBand();
		
		// time code to stop file overwrite
		DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("HHmmss");
		String timeStamp = timeStampPattern.format(java.time.LocalDateTime.now());
		
		String filename = String.join(".", Arrays.asList(objectId, filterBand, timeStamp, "fits"));

		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "radec");
		dir.mkdirs();
		File file = new File(dir, filename);
		return file.toString();
	}


	public static void main(String[] args) {
		CatalogQuery query = new CatalogQuery();
		
		query.setCatalogType(CatalogMagType.DSS);
		
		String s = downloadDssFits(query);
		
		System.out.println(s);
	}

}


