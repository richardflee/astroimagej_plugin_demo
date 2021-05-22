package rfl.astroimagej.exceptions;

/**
 * Custom exception thrown when object name fails to match any SIMBAD database Object ID records
 *
 */
public class SimbadNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public SimbadNotFoundException(String message) {
		super(message);
	}
}
