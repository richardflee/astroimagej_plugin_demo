package rfl.astroimagej.dev.catalogs;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.enums.CatalogMagType;

public class CatalogFactory {

	public static AstroCatalog createCatalog(CatalogMagType catalogType) {
		AstroCatalog catalog = null;
		switch (catalogType) {
		case SIMBAD:
			break;
		case VSP:
			catalog = new VspCatalog();
			break;
		case APASS:
			String msg = "APASS not implemented";
			JOptionPane.showMessageDialog(null, msg, "SIMBAD Query Error", JOptionPane.INFORMATION_MESSAGE);
			break;
		}
		return catalog;
	}
}
