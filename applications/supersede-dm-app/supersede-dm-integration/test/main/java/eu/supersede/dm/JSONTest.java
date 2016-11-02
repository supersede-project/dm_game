package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import eu.supersede.dm.datamodel.Alert;
import eu.supersede.dm.datamodel.Condition;
import eu.supersede.dm.datamodel.DataID;
import eu.supersede.dm.datamodel.Operator;
import eu.supersede.dm.datamodel.RequestClassification;
import eu.supersede.dm.datamodel.UserRequest;

public class JSONTest {
	
	public static void main( String[] args ) {
		
		List<Condition> conditions = new ArrayList<>();
		
		conditions.add( new Condition( DataID.UNSPECIFIED, Operator.GEq, 10.5 ) );
		
		List<UserRequest> requests = new ArrayList<>();
		
		requests.add( new UserRequest( 
				"id1", 
				RequestClassification.FeatureRequest, 
				0.5, "description string", 1, 2, 0, 
				new String[] { "feedbackId1" }, 
				new String[] { "UI", "backend" } ) );
		
		Alert alert = new Alert( 
				"id1", "appId1", System.currentTimeMillis(), "Delta", conditions, requests );
		
		Gson gson = new Gson();
		
		System.out.println( gson.toJson( alert ) );
		
	}
	
}
