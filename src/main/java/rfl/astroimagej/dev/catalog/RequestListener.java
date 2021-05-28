/**
 * 
 */
package rfl.astroimagej.dev.catalog;

/**
 * Listener for download astronomical catalog requests when user selects download option in catalogs form
 *
 */
@FunctionalInterface
public interface RequestListener {
	public void requestPerformed(RequestEvent event);
}
