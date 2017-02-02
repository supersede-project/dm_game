package eu.supersede.dm.tester;

import java.util.ArrayList;
import java.util.Random;

import eu.supersede.dm.agent.RESTClient;
import eu.supersede.integration.api.dm.types.Alert;
import eu.supersede.integration.api.dm.types.RequestClassification;
import eu.supersede.integration.api.dm.types.UserRequest;


public class AlertSimulator {
	
	public static void main( String[] args ) {
		new AlertSimulator().run( "http://127.0.0.1:80" );
	}
	
	String[] words = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum".split( "[ ]" );
	
	Random r = new Random( System.currentTimeMillis() );
	
	String createRandomDescription() {
		String text = "";
		for( int i = 0; i < 3 + r.nextInt( 10 ); i++ ) {
			text += words[ r.nextInt( words.length ) ] + " ";
		}
		return text.trim();
	}
	
	RequestClassification createRandomRequestClassification() {
		int c = r.nextInt( 3 );
		switch( c ) {
		case 0: return RequestClassification.BugFixRequest;
		case 1: return RequestClassification.EnhancementRequest;
		case 2: return RequestClassification.FeatureRequest;
		default: return RequestClassification.FeatureRequest;
		}
	}
	
	public void run( String base_addr ) {
		
		Alert alert = new Alert( "id1", "1", System.currentTimeMillis(), "atos", new ArrayList<>(), new ArrayList<>() );
		
		for( int c = 0; c < 1 + r.nextInt( 4 ); c++ ) {
			alert.getRequests().add( 
					new UserRequest( 
							"" + System.currentTimeMillis(), 
							createRandomRequestClassification(), 
							r.nextDouble(), 
							createRandomDescription(), 
							r.nextInt( 5 ), 
							r.nextInt( 5 ), 
							r.nextInt( 5 ), 
							new String[] {}, 
							new String[] {} ) );
		}
		
		RESTClient client = new RESTClient( base_addr );
		
		client.post( "supersede-dm-app/api/public/monitoring/alert" ).
			header( "Content-Type", "application/json;charset=UTF-8" ).
			send( alert );
		
	}
	
}
