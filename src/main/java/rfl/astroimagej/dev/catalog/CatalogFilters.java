package rfl.astroimagej.dev.catalog;

public enum CatalogFilters {
	VSP("B.V.Rc.Ic"), 	APASS("B.V.SR.SG");
	
private String filters;	
	CatalogFilters (String filters) {
		this.filters = filters;
	}
	
	public String getFilters() {
		return this.filters;
	}
}
