package rfl.astroimagej.dev.enums;

/**
 * 
 * Enums map SIMBAD query parameters to respective url segments; append segment to url in a single parameter
 * data request
 *
 */
public enum SimbadDataType {
	USER_TARGET_NAME(""), RA_HR("ra(d;ICRS;2000.0;2000.0)"), DEC_DEG("dec(d;ICRS;2000.0;2000.0)"), MAG_B("flux(B)"),
	MAG_V("flux(V)"), MAG_Rc("flux(R)"), MAG_Ic("flux(I)");

	private String urlFragment;

	SimbadDataType(String urlFragment) {
		this.urlFragment = urlFragment;
	}

	public String getUrlFragment() {
		return this.urlFragment;
	}
	
		public static void main(String[] args) {		
		String s = "* alf Lyr";		
		String rx = "[^a-zA-Z0-9\\s\\w\\-\\.]";		
		s = s.replaceAll(rx, "");		
		System.out.println(s.replaceAll(rx, ""));		
	}
}

