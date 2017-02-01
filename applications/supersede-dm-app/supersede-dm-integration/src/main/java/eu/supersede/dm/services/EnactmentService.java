package eu.supersede.dm.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import eu.supersede.dm.agent.ClientAgent;
import eu.supersede.dm.agent.RESTClient;
import eu.supersede.dm.datamodel.Feature;
import eu.supersede.dm.datamodel.FeatureList;
import eu.supersede.integration.api.replan.controller.proxies.IReplanController;
import eu.supersede.integration.api.replan.controller.proxies.ReplanControllerProxy;
import eu.supersede.integration.api.replan.controller.types.AddFeaturesForProjectPayload;
import eu.supersede.integration.api.replan.controller.types.FeatureWP3;
import eu.supersede.integration.api.replan.controller.types.Priority;

public class EnactmentService {

	private static EnactmentService instance = new EnactmentService();

	public static EnactmentService get() {
		return instance;
	}

	/*
	 * Temporary Method - to be finalized
	 */
	public void send( FeatureList features, boolean useIF ) {

		List<FeatureWP3> list = new ArrayList<>();

		for( Feature f : features.list() ) {
			try {
				FeatureWP3 fwp3 = new FeatureWP3();
				fwp3.setArguments( "" );
				fwp3.setDescription( "" );
				fwp3.setEffort( 1.0 );
				fwp3.setHardDependencies( new ArrayList<>() );
				fwp3.setId( Integer.parseInt( f.getId() ) );
				fwp3.setName( f.getName() );
				fwp3.setPriority( getPriorityEnum( f.getPriority() ) );
				fwp3.setRequiredSkills( new ArrayList<>() );
				fwp3.setSoftDependencies( new ArrayList<>() );
				list.add( fwp3 );
			}
			catch( Exception ex ) {
				System.err.println( "Skip feature with ID: " + f.getId() + " (" + f.getName() + ")" );
			}
		}

		try {
			AddFeaturesForProjectPayload p = new AddFeaturesForProjectPayload();
			p.setConstraints( new ArrayList<>() );
			p.setEvaluationTime( "" );
			p.setFeatures( list );

			if( useIF ) {
				IReplanController replan = new ReplanControllerProxy();
				replan.addFeaturesToProjectById( p, 0 );
			}
			else {
				
				String json = new Gson().toJson( p );
				
				json = json.replaceAll( "\\bevaluationTime\\b", "evaluation_time" );
				json = json.replaceAll( "\\bhardDependencies\\b", "hard_dependencies" );
				json = json.replaceAll( "\\bsoftDependencies\\b", "soft_dependencies" );
				
				System.out.println( json );
				
				RESTClient client = new RESTClient( "http://supersede.es.atos.net:8280/replan" );
				
				client.post( "projects/1/features" )
				.header( "Content-Type", "application/json" )
				.header( "Cache-Control", "no-cache" )
				.send( json );
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Priority getPriorityEnum( int priority ) {
		switch( priority ) {
		case 5: return Priority.FIVE;
		case 4: return Priority.FOUR;
		case 3: return Priority.THREE;
		case 2: return Priority.TWO;
		case 1: return Priority.ONE;
		default: return Priority.ONE;
		}
	}

}
