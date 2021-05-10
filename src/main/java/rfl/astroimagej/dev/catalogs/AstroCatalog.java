package rfl.astroimagej.dev.catalogs;

import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.QueryResult;

@FunctionalInterface
public interface AstroCatalog {
	public QueryResult runQuery(CatalogQuery query);
}
