package rfl.astroimagej.dev.catalog_ui;

import rfl.astroimagej.dev.enums.TextFieldType;

public class TextFieldVerifier {

	// append to regex, optional trailing decimal places up to .nnnn
	private static String rxDecimal = "(?:[.]\\d{0,4})?";
	
	// no args constructor
	public TextFieldVerifier() {}

	/**
	 * Returns true if user input in ChartUI text field is valid, otherwise false
	 * <p>
	 * Refer  to isValid functions for acceptable data range and formats
	 * </p>
	 * 
	 * @param input user input, trimmed of any external white space characters
	 * @param textFieldName lowercase string identifying input text field
	 * 
	 * @return true if data complies with data range and format, false otherwise
	 */
	protected boolean verifyInput(String input, TextFieldType dataType) {
		
		// apply validation of active field data
		boolean isValid = true;
		if (dataType == TextFieldType.OBJECT_ID) {
			isValid = isValidObjectId(input);
			
		} else if (dataType == TextFieldType.RA_HMS) {
			isValid = isValidCoords(input, TextFieldType.RA_HMS);

		} else if (dataType == TextFieldType.DEC_DMS) {
			isValid = isValidCoords(input, TextFieldType.DEC_DMS);

		} else if (dataType == TextFieldType.FOV_AMIN) {
			isValid = isValidFov(input);

		} else if (dataType == TextFieldType.MAG_LIMIT) {
			isValid = isValidMagLimit(input);
		}
		return isValid;
	}
	
	/**
	 * Test if format of format target name is alphanumeric
	 * 
	 * @param input target name
	 * @return true for alphanumeric chars or white-space, '.', '-' and '_' chars,
	 *         false otherwise
	 */
	protected static boolean isValidObjectId(String input) {
		String rx = "^[a-zA-Z0-9\\s\\w\\-\\.]+$";
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/**
	 * Check user input of ra or dec coordinates conform
	 * 
	 * <p>
	 * Ra format: 00:00:00[.00] to 23:59:59[.00] where [.00] indicates optional
	 * decimal places, RA in units hours
	 * </p>
	 * <p>
	 * Dec format ±90:00:00[.00] where [.00] indicates optional decimal places Dec
	 * in units degree
	 * </p>
	 * 
	 * @param input user input ra or dec values in sexagesimal format
	 * @param radec RA or DEC flag
	 * 
	 * @return true if input conforms to relevant format, false otherwise
	 */
	protected static boolean isValidCoords(String input, TextFieldType radec) {
		// delete any whitespace chars
		input = input.replaceAll("\\s+", "");

		// hrs regex 0 - 9 or 00 - 23 + ':' delim
		String rxHr = "([0-2]|[0-1][0-9]|2[0-3]):";

		// deg regex 0 - 9 or 00 - 90
		String rxDeg = "([0-9]|[0-8][0-9]|90):";

		// min regex 0 - 9 or 00 - 59 + ':' delim
		String rxMm = "([0-9]|[0-5][0-9]):";

		// ss regex = mm regex less delim char
		String rxSs = rxMm.substring(0, rxMm.length() - 1);

		String rx = rxDecimal;
		if (radec == TextFieldType.RA_HMS) {
			rx = "^[+]?" + rxHr + rxMm + rxSs + rx;
		} else if (radec == TextFieldType.DEC_DMS) {
			rx = "^[+-]?" + rxDeg + rxMm + rxSs + rx;
		}
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/*
	 * Test if Field-of-View (fov) value is in range
	 * 
	 * @param input width of FOV
	 * @return true if input in range 1.0 to 1200, false otherwise
	 */
	protected static boolean isValidFov(String input) {
		// compile regex range 1.0 to 1200] (arcmin)
		// optional leading '+' sign and up to 4 decimal places
		String rx = "([1-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1][0-1][0-9][0-9])";
		rx = "^[+]?" + rx + rxDecimal;
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/*
	 * Test if max mag value is in range
	 * 
	 * @param input maximum object mag to download from on-line catalog
	 * @return true if input in range 1.0 to 99, false otherwise
	 */
	protected static boolean isValidMagLimit(String input) {
		// compile regex range 1.0 to 99.99] (mag)
		// optional leading '+' sign and up to 4 decimal places
		String rx = "([1-9]|[1-9][0-9])";
		rx = "^[+]?" + rx + rxDecimal;
		return (input.trim().length() > 0) && input.matches(rx);
	}
	
	
}
