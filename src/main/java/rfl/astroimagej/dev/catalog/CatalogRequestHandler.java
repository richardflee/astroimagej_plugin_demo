package rfl.astroimagej.dev.catalog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import rfl.astroimagej.dev.utils.CatalogUtils;

/**
 * Handles RequestEvents to save current catalog request params and download
 * on-line catalog data
 * 
 * <p>
 * Event is triggered when user selects 'download' in catalog form
 *
 */
public class CatalogRequestHandler implements CatalogRequestListener {

	// populates properties file with default params
	public static void main(String[] args) {

		String targetName = "WASP-12";
		String raSexagesimal = "06:30:32.80";
		String decSexagesimal = "+29:40:20.27";
		String fovAmin = "60.0";
		String magLimit = "17.0";
		String filter = "V";
		String vspFilters = "V.B.Rc.Ic";

		CatalogRequestEvent evt = new CatalogRequestEvent(targetName, raSexagesimal, decSexagesimal, fovAmin, magLimit, filter,
				vspFilters);
		System.out.println(evt.toString());

		// test with CatalogRequestHandler instance
		CatalogRequestHandler handler = new CatalogRequestHandler();
		handler.requestPerformed(evt);
	}

	// save current dialog data to .astroimage.properties
	/**
	 * Saves current dialog data to .astroimage.properties file in user.home
	 * directory
	 * 
	 * @param event params for on-line astro catalog download request
	 */
	@Override
	public void requestPerformed(CatalogRequestEvent event) {
		writeVspProperties(event);
	}

	/**
	 * Method to write form settings to vspdemo.properties in user.home/.astroimagej
	 * folder
	 * <p>
	 * * The list of available vsp filters is fixed. To add a new filter to the
	 * list, edit the properties file to include new filter option. E.g. to add
	 * filter X to the end of the list, append '.X' to the vspfilters property
	 * (vspfilters=V.B.Rc.Ic.X)
	 * </p>
	 * 
	 * @param event event object encapsulating form data inputs
	 */
	private void writeVspProperties(CatalogRequestEvent event) {

		try (OutputStream output = new FileOutputStream(CatalogUtils.getPropsPath())) {
			Properties prop = new Properties();

			prop.setProperty("target", event.getTargetName());
			prop.setProperty("ra", event.getRaSexagesimal());
			prop.setProperty("dec", event.getDecSexagesimal());
			prop.setProperty("fov", event.getFovAmin());
			prop.setProperty("magLimit", event.getMagLimit());
			prop.setProperty("filter", event.getFilter());
			prop.setProperty("vspfilters", event.getVspFilters());

			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		}

	}
}
