package eu.supersede.dm.datamodel;

import java.util.List;

public class Alert {
	
	public Alert(String iD, String applicationID, long timestamp, String tenant, String resourceID, List<Condition> conditions) {
		super();
		ID = iD;
		this.applicationID = applicationID;
		this.timestamp = timestamp;
		this.tenant = tenant;
		this.resourceID = resourceID;
		this.conditions = conditions;
	}


	String				ID;
	String				applicationID;
	long				timestamp;
	String				tenant;
	String				resourceID;
	List<Condition>		conditions;
	
	
	public Alert() {}
	
	
}
