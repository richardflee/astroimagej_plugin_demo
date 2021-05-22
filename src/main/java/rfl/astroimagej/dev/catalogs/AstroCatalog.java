package rfl.astroimagej.dev.catalogs;

/**
 * Interface implemented by Variable Star Plotter and other catalogs 
 */
@FunctionalInterface
public interface AstroCatalog {
	/**
	 * @param query catalog query request parameters
	 * 
	 * @return QueryResult object encapsulating results of catalog query
	 */
	public QueryResult runQuery(CatalogQuery query);
}
