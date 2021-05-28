package rfl.astroimagej.dev.queries;

import java.util.ArrayList;

/**
 * Encapsulates the results of a coordinate-based query on the VSP or other database
 * in an list of FieldObjects
 */
import java.util.List;

public class QueryResult {

	// object data
	private String objectId = null;
		
	// Vsp query
	private List<FieldObject> fieldObjects = null;

	public QueryResult(String objectId) {
		this.objectId = objectId;		
		this.fieldObjects = new ArrayList<>();
	}
	
	// ** List<FieldObject> customer getter / setters
	public List<FieldObject> getFieldObjects() {
		return fieldObjects;
	}

	public void setFieldObject(FieldObject fieldObject) {
		fieldObjects.add(fieldObject);
	}	
	
	// read-only objectId
	public String getObjectId() {
		return objectId;
	}

	@Override
	public String toString() {
		return "QueryResult [objectId=" + objectId + ", fieldObjects=" + fieldObjects + ", getFieldObjects()="
				+ getFieldObjects() + ", getObjectId()=" + getObjectId() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
}
