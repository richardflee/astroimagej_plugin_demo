/**
 * 
 */
package rfl.astroimagej.dev.catalog;

/**
 * Listener for download astronomical catalog requests when user selects download option in catalogs form
 *
 */
@FunctionalInterface
public interface CatalogRequestListener {
	public void requestPerformed(CatalogRequestEvent event);
}
