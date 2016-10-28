package eu.supersede.dm.datamodel;

import java.util.List;

public class Alert {
	
	String				ID;
	String				applicationID;
	long				timestamp;
	String				tenant;
	List<Condition>		conditions;
	List<UserRequest>	requests;
	
	
	public Alert(	String iD, String applicationID, long timestamp, 
					String tenant, String resourceID, List<Condition> conditions, List<UserRequest>	requests ) {
		super();
		ID = iD;
		this.applicationID = applicationID;
		this.timestamp = timestamp;
		this.tenant = tenant;
		this.conditions = conditions;
		this.requests = requests;
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


	public List<Condition> getConditions() {
		return conditions;
	}


}
