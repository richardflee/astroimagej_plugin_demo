package rfl.astroimagej.dev.catalogs;

import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.QueryResult;
import rfl.astroimagej.exceptions.SimbadNotFoundException;

@FunctionalInterface
public interface AstroCatalog {
	public QueryResult runQuery(CatalogQuery query) throws SimbadNotFoundException;
}
