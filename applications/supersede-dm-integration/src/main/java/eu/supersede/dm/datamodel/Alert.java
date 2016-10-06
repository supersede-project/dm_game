package eu.supersede.dm.datamodel;

import java.util.List;

public class Alert {
	
	String				ID;
	String				applicationID;
	long				timestamp;
	String				tenant;
	String				resourceID;
	List<Condition>		conditions;
	
	
	public Alert(	String iD, String applicationID, long timestamp, 
					String tenant, String resourceID, List<Condition> conditions ) {
		super();
		ID = iD;
		this.applicationID = applicationID;
		this.timestamp = timestamp;
		this.tenant = tenant;
		this.resourceID = resourceID;
		this.conditions = conditions;
	}


	public Alert() {}
	
	
	public String getID() {
		return ID;
	}


	public String getApplicationID() {
		return applicationID;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public String getTenant() {
		return tenant;
	}


	public String getResourceID() {
		return resourceID;
	}


	public List<Condition> getConditions() {
		return conditions;
	}


}
