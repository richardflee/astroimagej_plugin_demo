package rfl.astroimagej.dev.fileio;

import rfl.astroimagej.dev.catalogs.CatalogQuery;

/**
 * Interface implemented by Vsp_demo file writer methods 
 */
public interface FileWriterListener {	
	/**
	 * @param query user input parameters to query on-line catalog database 
	 * 
	 * @return message if succesful processing the query
	 */
	public String writeFile(CatalogQuery query);

}
