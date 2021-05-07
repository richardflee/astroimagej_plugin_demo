package rfl.astroimagej.dev.queries;

import rfl.astroimagej.dev.enums.CatalogMagType;

public class CatalogQuery {
	// object data
	private String objectId = null;
	private Double raHr = null;
	private Double decDeg = null;
	private Double fovAmin = null;
	private Double magLimit = null;
	private CatalogMagType catalogType = null;
	private String magBand = null;
	
	public CatalogQuery() {
		this.objectId = "wasp 12";
		this.raHr = 6.50911;
		this.decDeg = 29.67230;
		this.fovAmin = 60.0;
		this.magLimit = 15.0;
		this.catalogType = CatalogMagType.SIMBAD;
		this.magBand = "V";
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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

	public Double getFovAmin() {
		return fovAmin;
	}

	public void setFovAmin(Double fovAmin) {
		this.fovAmin = fovAmin;
	}

	public Double getMagLimit() {
		return magLimit;
	}

	public void setMagLimit(Double magLimit) {
		this.magLimit = magLimit;
	}

	public CatalogMagType getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(CatalogMagType catalogType) {
		this.catalogType = catalogType;
	}

	public String getMagBand() {
		return magBand;
	}

	public void setMagBand(String magBand) {
		this.magBand = magBand;
	}

	@Override
	public String toString() {
		return "CatalogQuery [objectId=" + objectId + ", raHr=" + raHr + ", decDeg=" + decDeg + ", fovAmin=" + fovAmin
				+ ", magLimit=" + magLimit + ", catalogType=" + catalogType + ", magBand=" + magBand + "]";
	}


}

