package rfl.astroimagej.dev.catalog;

public enum AstroCatalog {
	VSP("B.V.Rc.Ic"), 	APASS("B.V.SR.SG");
	
private String filters;	
	AstroCatalog (String filters) {
		this.filters = filters;
	}
	
	public String getFilters() {
		return this.filters;
	}
}
