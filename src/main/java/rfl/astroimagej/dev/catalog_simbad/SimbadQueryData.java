package rfl.astroimagej.dev.catalog_simbad;

import rfl.astroimagej.dev.query_base.AstroObject;

/**
 * 
 * Class to handle object star identifier queries to SIMBAD on-line astronomical database.
 * <p>
 * Default parameters are the results for a query on SIMBAD identifier WASP-12
 * </p>
 *
 */
public class SimbadQueryData extends AstroObject {	
	private Double magB;
	private Double magV;
	private Double magRc;
	private Double magIc;
	
	public SimbadQueryData() {
		super();
		this.magB = 12.14;
		this.magV = 11.57;
		this.magRc = null;
		this.magIc = null;		
	}
	
	/**
	 * Single parameter constructor for user input identifier
	 * 
	 * @param identifier user input object identifier
	 */
	public SimbadQueryData(String identifier) {
		super(identifier);
	}

	public Double getMagB() {
		return magB;
	}

	public void setMagB(Double magB) {
		this.magB = magB;
	}

	public Double getMagV() {
		return magV;
	}

	public void setMagV(Double magV) {
		this.magV = magV;
	}

	public Double getMagRc() {
		return magRc;
	}

	public void setMagRc(Double magRc) {
		this.magRc = magRc;
	}

	public Double getMagIc() {
		return magIc;
	}

	public void setMagIc(Double magIc) {
		this.magIc = magIc;
	}

	@Override
	public String toString() {
		String s = "SimbadQueryData [magB=" + magB + ", magV=" + magV + ", magRc=" + magRc + ", magIc=" + magIc + "]";
		s += super.toString();
		return s;
	}

}

