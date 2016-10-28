package eu.supersede.dm.services;

import eu.supersede.dm.agent.RESTClient;
import eu.supersede.dm.datamodel.Feature;

public class DecisionMakingService {
	
	private static DecisionMakingService instance = new DecisionMakingService();
	
	public static DecisionMakingService get() {
		return instance;
	}
	
	/*
	 * Temporary Method - to be finalized
	 */
	public void setScheduled( Feature feature, String addr ) {
		
		RESTClient client = new RESTClient( addr );
		
		client.post( "api/public/schedule" ).
		header( "Content-Type", "application/json;charset=UTF-8" ).
		send( feature );
		
	}
	
}
