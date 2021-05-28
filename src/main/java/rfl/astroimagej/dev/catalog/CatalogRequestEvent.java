package rfl.astroimagej.dev.catalog;

/**
 * Event encapsulating astro catalog query parameters
 * <p>
 * Parameters are saved to vspdemo.properties file in user.home/.astroimagej directory
 *
 */

public class CatalogRequestEvent {
	private String targetName;
	private String raSexagesimal;
	private String decSexagesimal;
	private String fovAmin;
	private String magLimit;
	private String filter;
	private String vspFilters;

	/**
	 * 
	 * @param targetName     object name, optionally in VSP catalog
	 * @param raSexagesimal  J2000 RA in HH:MM:SS format
	 * @param decSexagesimal J2000 Dec in ±DD:MM:SS format
	 * @param fovAmin        full field in arcmin
	 * @param magLimit       maximum reference object magnitude in catalog request
	 * @param filter         photometric filter type
	 * @param vspFilters     combo list of VSP photometric filter set with '.' delimiter
	 */
	public CatalogRequestEvent(String targetName, String raSexagesimal, String decSexagesimal, 
			String fovAmin, String magLimit, String filter, String vspFilters) {
		super();
		this.targetName = targetName;
		this.raSexagesimal = raSexagesimal;
		this.decSexagesimal = decSexagesimal;
		this.fovAmin = fovAmin;
		this.magLimit = magLimit;
		this.filter = filter;
		this.vspFilters = vspFilters;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getRaSexagesimal() {
		return raSexagesimal;
	}

	public void setRaSexagesimal(String raSexagesimal) {
		this.raSexagesimal = raSexagesimal;
	}

	public String getDecSexagesimal() {
		return decSexagesimal;
	}

	public void setDecSexagesimal(String decSexagesimal) {
		this.decSexagesimal = decSexagesimal;
	}

	public String getFovAmin() {
		return fovAmin;
	}

	public void setFovAmin(String fovAmin) {
		this.fovAmin = fovAmin;
	}

	public String getMagLimit() {
		return magLimit;
	}

	public void setMagLimit(String magLimit) {
		this.magLimit = magLimit;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getVspFilters() {
		return vspFilters;
	}

	public void setVspFilters(String vspFilters) {
		this.vspFilters = vspFilters;
	}

	@Override
	public String toString() {
		return "RequestEvent [targetName=" + targetName + ", raSexagesimal=" + raSexagesimal + ", decSexagesimal="
				+ decSexagesimal + ", fovAmin=" + fovAmin + ", magLimit=" + magLimit + ", filter=" + filter
				+ ", vspFilters=" + vspFilters + "]";
	}
	
	

}
