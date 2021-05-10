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

import rfl.astroimagej.dev.queries.CatalogQuery;

public class SkyViewCatalog {
	private final String nPixels = "1000"; 
	private CatalogQuery query = null;
	
	public SkyViewCatalog() {
		
	}
	
	public void runQuery(CatalogQuery query) {	
		this.query = query;
		System.out.println(getUrl());
		String filePath = getFilePath(query);
		String skyUrl = getUrl();
		
		try {
			InputStream in = new URL(skyUrl).openStream();
			Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String getUrl() {
		// skyview header
		String url = "https://skyview.gsfc.nasa.gov/cgi-bin/images?Survey=digitized+sky+survey"; 
		
		// chart centre coords = ra (deg) & dec (deg)
		url += String.format("&position=%.5f,%.5f", query.getRaHr() * 15.0, query.getDecDeg());
		
		// fov in deg
		url += String.format("&Size=%s", query.getFovAmin() / 60.0);
		
		// number pixels and append FITS file type
		url += String.format("&Pixels=%s&Return=FITS", nPixels);
		return url;
	}
	
	private String getFilePath(CatalogQuery query) {
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
		SkyViewCatalog skyview = new SkyViewCatalog();
		
		CatalogQuery query = new CatalogQuery();
		
		skyview.runQuery(query);

	}

}


