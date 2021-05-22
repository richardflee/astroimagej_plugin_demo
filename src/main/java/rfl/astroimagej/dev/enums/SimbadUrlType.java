package rfl.astroimagej.dev.enums;

/**
 * Enum maps url segments to SIMBAD query type
 */
public enum SimbadUrlType {
	USER_TARGET_NAME(""), 
	RA_HR("ra(d;ICRS;2000.0;2000.0)"), 
	DEC_DEG("dec(d;ICRS;2000.0;2000.0)"), 
	MAG_B("flux(B)"),
	MAG_V("flux(V)"), 
	MAG_R("flux(R)"), 
	MAG_I("flux(I)");

	private String paramUrl;

	SimbadUrlType(String urlParam) {
		this.paramUrl = urlParam;
	}

	public String getUrlFragment() {
		return this.paramUrl;
	}
}
