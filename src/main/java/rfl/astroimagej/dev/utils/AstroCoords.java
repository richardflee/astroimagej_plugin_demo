package rfl.astroimagej.dev.utils;

import rfl.astroimagej.dev.enums.TextFieldType;

/**
 * Class methods to convert between sexagesimal and numeric formats
 */
public class AstroCoords {
	
	/**
	 * Convert numeric ra (hh.hhhh) to sexagesimal format.
	 * 
	 * <p>Ra can be positive or negative and unlimited magnitude</p>
	 *
	 * @param raHr in numeric format in units hr
	 * 
	 * @return ra in sexagesimal format HH:MM:SS.SS
	 */
	public static String raHr_To_raHms(Double raHr) {
		// coerce input data into range 0..24 (hr)
		double data = (raHr >= 0) ? raHr % 24 : (24 + raHr % 24);
		
		// extract hh, mm and ss terms
		int hh = (int) (1.0 * data);
		int mm = (int) ((data - hh) * 60);
		Double ss = 3600 * (data - hh) - 60 * mm;
		
		// compile, format and return sexagesimal ra
		return String.format("%02d", hh) + ":" 
				+ String.format("%02d", mm) + ":"
				+ String.format("%5.2f", ss).replace(' ', '0');
	}
	
	
	/**
	 * Convert numeric dec (dd.dddd) to sexagesimal format.
	 * 
	 * <p>Dec can be positive or negative. Magnitude exceeding 90 is clipped to ±90.</p>
	 * 
	 * @param decDeg in numeric format, where dec is in units deg
	 * 
	 * @return dec in sexagesimal format DD:MM:SS.SS
	 */
	public static String decDeg_To_decDms(Double decDeg) {
		String sign = (decDeg >= 0) ? "+" : "-";
		
		// coerce input data into range ±90.0
		double data = (Math.abs(decDeg) > 90.0) ? 90.0 : Math.abs(decDeg);
		
		// extract dd, mm, ss terms
		int dd = (int) (1.0 * data);
		int mm = (int) ((data - dd) * 60);
		Double ss = ((data - dd) * 60 - mm) * 60;
		
		// compile, format and return sexagesimal dec
		return sign + String.format("%02d", dd) + ":" 
				+ String.format("%02d", mm) + ":"
				+ String.format("%5.2f", ss).replace(' ', '0');
	}
	
	
	/**
	 * Convert ra sexagesimal format to numeric value (hours). 
	 * 
	 * <p> Negative ra is converted to 24 - |ra| </p>
	 * 
	 * @param raHms in sexagesimal format HH:MM:SS.SS
	 * 
	 * @return numeric ra in units hr (hh.hhhh)
	 */
	public static Double raHms_To_raHr(String raHms) {

		boolean isNegative = raHms.charAt(0) == '-';
		
		// split input at ':' delim and coerce elements into appropriate range
		String[] el = raHms.split(":");
		double hh = Math.abs(Double.valueOf(el[0]));
		double mm = Double.valueOf(el[1]) % 60;
		double ss = Double.valueOf(el[2]) % 60;
		double raHr = (hh + mm / 60 + ss / 3600) % 24;
		return isNegative ? (24.0 - raHr) : raHr;
	}
	
	/**
	 * Convert dec sexagesimal format to numeric value (dd.dddd). 
	 * 
	 * @param decDms in sexagesimal format DD:MM:SS.SS
	 * 
	 * @return numeric dec in units deg (±dd.dddd)
	 */
	public static Double decDms_To_decDeg(String decDms) {
		int sign = (decDms.charAt(0) == '-') ? -1 : 1;
		
		// split input at ':' delim and coerce elements into appropriate range
		String[] el = decDms.split(":");
		double dd = Math.abs(Double.valueOf(el[0]));
		
		// clip |dec| > 90 to 90.0
		if (dd > 90) {
			return sign * 90.0;
		}
		double mm = Double.valueOf(el[1]) % 60;
		double ss = Double.valueOf(el[2]) % 60;
		return sign * (dd + mm / 60 + ss / 3600);
	}
	
	/**
	 * If data entry is RA or DEC field then formats input to sexagesimal format
	 * 
	 * @param input user input to RA or DEC fields
	 * 
	 * @param dataType identifies input field type(OBJECT_ID, RA_HMS .. )
	 * 
	 * @return ra or dec input in sexagesimal format HH:MM:SS.SS or DD:MM:SS.SS respectively
	 */
	public static String sexagesimalFormatter(String input, TextFieldType dataType) { 
		String sxInput = input;
		// converts sexagesimal => numeric => sexagesimal to force formatting
		if (dataType == TextFieldType.RA_HMS) {
			sxInput = AstroCoords.raHr_To_raHms(AstroCoords.raHms_To_raHr(input));
		} else if (dataType == TextFieldType.DEC_DMS) {
			sxInput = AstroCoords.decDeg_To_decDms(AstroCoords.decDms_To_decDeg(input));
		}		
		return sxInput;
	}
	
}

