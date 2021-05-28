package rfl.astroimagej.dev.query_base;

/**
 * Base class for astronomical catalog query objects. 
 *<p>
 * Class methods to validate ra and dec coordinate inputs and convert between sexagesimal and numeric formats
 *</p>
 *
 */
public abstract class AstroObject {
	
	private String identifier;
	private Double raHr;
	private Double decDeg;
	
	/**
	 * No arguments constructor with default WASP-12 parameters
	 */
	public AstroObject() {
		this.identifier = "WASP-12";
		this.raHr = 6.50911;
		this.decDeg = 29.67230;
	}
	
	/**
	 * Single argument constructor overwrites default with user input target name
	 * 
	 * @param identifier user-input name of astronomical object
	 */
	public AstroObject(String identifier) {
		this();
		this.identifier = identifier;
	}
	
	/**
	 * Convert numeric ra (hh.hhhh) to sexagesimal format.
	 * 
	 * <p>
	 * Ra can be positive or negative and unlimited magnitude
	 *</p>
	 *
	 * @param raHr in numeric format in units hr
	 * @return ra in sexagesimal format HH:MM:SS.SS
	 * 
	 */
	public static String raHr_To_raHms(Double raHr) {
		// coerce input data into range 0..24 (hr)
		double data = (raHr >= 0) ? raHr % 24 : (24 + raHr % 24);
		
		// extract hh, mm and ss terms
		int hh = (int) (1.0 * data);
		int mm = (int) ((data - hh) * 60);
		Double ss = 3600 * (data - hh) - 60 * mm;
		
		// compile, format and return sexagesimal ra
		return String.format("%02d", hh) + ":" + String.format("%02d", mm) + ":"
				+ String.format("%5.2f", ss).replace(' ', '0');
	}
	
	
	/**
	 * Convert numeric dec (dd.dddd) to sexagesimal format.
	 * Dec can be positive or negative. Magnitude exceeding 90 is clipped to ±90.
	 * <p>
	 * @param decDeg in numeric format, where dec is in units deg
	 * @return dec in sexagesimal format DD:MM:SS.SS
	 * </p>
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
		return sign + String.format("%02d", dd) + ":" + String.format("%02d", mm) + ":"
				+ String.format("%5.2f", ss).replace(' ', '0');
	}
	
	
	/**
	 * Convert ra sexagesimal format to numeric value. 
	 * Negative ra is converted to 24 - |ra|.
	 * <p>
	 * @param raHms in sexagesimal format HH:MM:SS.SS
	 * @return numeric ra in units hr (hh.hhhh)
	 * </p>
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
	 * <p>
	 * @param decDms in sexagesimal format DD:MM:SS.SS
	 * @return numeric dec in units deg (±dd.dddd)
	 * </p>
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
	

	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public Double getRaHr() {
		return raHr;
	}


	public void setRaHr(Double raHr) {
		this.raHr = raHr;
	}


	public Double getDecDeg() {
		return decDeg;
	}


	public void setDecDeg(Double decDeg) {
		this.decDeg = decDeg;
	}


	@Override
	public String toString() {
		return "AstroObject [identifier=" + identifier + ", raHr=" + raHr + ", decDeg=" + decDeg + "]";
	}

}

