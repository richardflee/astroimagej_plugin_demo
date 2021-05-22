package rfl.astroimagej.dev.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Catalog form text field identifiers with associated lower case url's
 */
public enum TextFieldType {
	OBJECT_ID("objectIdField".toLowerCase()), 
	RA_HMS("raField".toLowerCase()),
	DEC_DMS("decField".toLowerCase()), 
	FOV_AMIN("fovAminField".toLowerCase()), 
	MAG_LIMIT("magLimitField".toLowerCase());
	
	private String fieldName;
	
	TextFieldType(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}
	
	// map to lookup enum by value
	// refer https://www.baeldung.com/java-enum-values
	private static final Map<String, TextFieldType> BY_FORMDATA = new HashMap<>();
	static {
        for (TextFieldType dataType: TextFieldType.values()) {
        	BY_FORMDATA.put(dataType.fieldName, dataType);
        }
    }
	
	/**
	 * (Reverse) look-up formTextField enum value from the associated fieldName (text)
	 * 
	 * @param fieldName enum field string, e.g. "objectid" for OBJECT_ID enum
	 * @return enum associated with dieldName (fieldName "objectid" => OBJECT_ID)
	 */
	public static TextFieldType valueOfFieldName(String fieldName) {
        return BY_FORMDATA.get(fieldName);
    }
	
	public static void main(String[] args) {
		String fieldName = TextFieldType.OBJECT_ID.getFieldName();
		
		boolean b = TextFieldType.RA_HMS == TextFieldType.valueOfFieldName(fieldName);
		System.out.println(b);
		
		for (TextFieldType dataType : TextFieldType.values()) {
			System.out.println(dataType.getFieldName());
		}
		
	}
}
