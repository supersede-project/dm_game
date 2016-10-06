package eu.supersede.dm.services;

import com.google.gson.Gson;

import eu.supersede.dm.agent.RESTClient;
import eu.supersede.dm.datamodel.UserRequestList;

public class MonitoringService {

	private static MonitoringService instance = new MonitoringService();
	
	public static MonitoringService get() {
		return instance;
	}
	
	/*
	 * Temporary Method - to be finalized
	 */
	public UserRequestList get( String resId, String addr ) {
		
		RESTClient client = new RESTClient( addr );
		
		String res = client.get( "path/to/rest" ).
		header( "Content-Type", "application/json;charset=UTF-8" ).
		send();
		
		UserRequestList list = new Gson().fromJson( res, UserRequestList.class );
		
		return list;
		
	}
	
}
