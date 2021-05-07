package rfl.astroimagej.dev.enums;

public enum CatalogMagType {
	SIMBAD("B.V.R.I"), VSP("B.V.Rc.Ic"), 	APASS("B.V.SR.SG");
	
private String magBand;	
	CatalogMagType (String magBand) {
		this.magBand = magBand;
	}
	
	public String getMagBand() {
		return this.magBand;
	}
}
