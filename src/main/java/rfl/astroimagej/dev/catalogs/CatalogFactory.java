package rfl.astroimagej.dev.catalogs;

import rfl.astroimagej.dev.enums.CatalogMagType;

public class CatalogFactory {

	public static AstroCatalog createCatalog(CatalogMagType catalogType) {
		AstroCatalog catalog = null;
		switch (catalogType) {
		case SIMBAD:
			catalog = new SimbadCatalog();
			break;
		case VSP:
			catalog = new VspCatalog();
			break;
		case APASS:
			System.out.println("APASS not implemented");
			break;
		}
		return catalog;
	}
}
