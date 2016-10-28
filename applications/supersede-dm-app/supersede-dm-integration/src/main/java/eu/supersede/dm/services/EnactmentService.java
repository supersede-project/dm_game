package eu.supersede.dm.services;

import eu.supersede.dm.agent.RESTClient;
import eu.supersede.dm.datamodel.FeatureList;

public class EnactmentService {
	
	private static EnactmentService instance = new EnactmentService();
	
	public static EnactmentService get() {
		return instance;
	}
	
	/*
	 * Temporary Method - to be finalized
	 */
	public void send( FeatureList features, String addr ) {
		
		RESTClient client = new RESTClient( addr );
		
		client.post( "api/public/alert" ).
		header( "Content-Type", "application/json;charset=UTF-8" ).
		send( features );
		
	}
	
}
