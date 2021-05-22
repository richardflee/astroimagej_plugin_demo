package rfl.astroimagej.dev.catalogs;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.enums.CatalogType;

/**
 * Creates an instance of catalog type selected in the catalog ui dialog.
 * <p>
 * currently only VSP catalog is the only implementation
 * <p/> 
 */
public class CatalogFactory {

	/**
	 * Manages selection of on-line astronomical database
	 * 
	 * @param catalogType type of on-line database to create
	 * @return catalog object of selected database type
	 */
	public static AstroCatalog createCatalog(CatalogType catalogType) {
		AstroCatalog catalog = null;
		if (catalogType == CatalogType.VSP) {
			catalog = new VspCatalog();
		} else {
			String message = String.format("Program error %s catalog", catalogType.toString());
			JOptionPane.showMessageDialog(null, message, "Catalog Query Error", JOptionPane.INFORMATION_MESSAGE);
		}
		return catalog;
	}
}
