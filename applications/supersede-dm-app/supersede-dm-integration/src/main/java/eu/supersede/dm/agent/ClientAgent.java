package eu.supersede.dm.agent;

import java.util.ArrayList;

import eu.supersede.dm.datamodel.Alert;


public class ClientAgent {
	
	public static void main( String[] args ) {
		new ClientAgent().run( "http://127.0.0.1:80" );
	}
	
	public void run( String base_addr ) {
		
		Alert alert = new Alert( "id1", "app1", System.currentTimeMillis(), "atos", new ArrayList<>(), new ArrayList<>() );
		
		RESTClient client = new RESTClient( base_addr );
		
//		System.out.println( "Basic " + new String( Base64.encodeBase64( "wp_admin:123456".getBytes() ) ) );
//		
//		String response = client.get( "user" ).
//			header( "TenantId", "atos" ).
//			header( "authorization", "Basic " + new String( Base64.encodeBase64( "wp_admin:123456".getBytes() ) ) ).
//			send();
//		
//		JsonObject o = new Gson().fromJson( response, JsonObject.class );
//		
//		String session = o.get( "details" ).getAsJsonObject().get( "sessionId" ).getAsString();
//		
//		System.out.println( session );
		
//		client.post( "api/public/alert" ).cookie( "SESSION", session ).send( alert );
		client.post( "game-requirements/api/public/alert" ).
		header( "Content-Type", "application/json;charset=UTF-8" ).
		send( alert );
		
//		System.out.println( client.post( "if/alert" ).cookie( "SESSION", user.getAuthorities(). ).send( alert ) );
		
	}
	
}
