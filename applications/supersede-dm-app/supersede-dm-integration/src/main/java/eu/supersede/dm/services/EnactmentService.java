package eu.supersede.dm.services;

import java.util.ArrayList;
import java.util.List;

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
	public void send( FeatureList features, String addr ) {
		
		IReplanController replan = new ReplanControllerProxy();
		
		List<FeatureWP3> list = new ArrayList<>();
		
		for( Feature f : features.list() ) {
			FeatureWP3 fwp3 = new FeatureWP3();
			fwp3.setArguments( "" );
			fwp3.setDescription( "" );
			fwp3.setEffort( 1.0 );
			fwp3.setHardDependencies( new ArrayList<>() );
			fwp3.setId( features.getProjectNum() );
			fwp3.setName( f.getName() );
			fwp3.setPriority( getPriorityEnum( f.getPriority() ) );
			fwp3.setRequiredSkills( new ArrayList<>() );
			fwp3.setSoftDependencies( new ArrayList<>() );
			list.add( fwp3 );
		}
		
		try {
			AddFeaturesForProjectPayload p = new AddFeaturesForProjectPayload();
			p.setConstraints( new ArrayList<>() );
			p.setEvaluationTime( "" );
			p.setFeatures( list );
			replan.addFeaturesToProjectById( p, 0 );
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
