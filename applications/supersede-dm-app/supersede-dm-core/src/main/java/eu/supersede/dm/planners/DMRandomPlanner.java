package eu.supersede.dm.planners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.IDMPlanner;

public class DMRandomPlanner implements IDMPlanner {

	@Override
	public DMSolution solve( DMProblem problem ) {
		
		Random random = new Random( System.currentTimeMillis() );
		
		int num = 1 + random.nextInt( 6 );
		
		DMSolution solution = new DMSolution();
		
		for( int i = 0; i < num; i++ ) {
			
			DMMethod method = problem.methods().get( random.nextInt( problem.methods().size() ) );
			
			Map<String,String> options = new HashMap<>();
			
			for( DMOption option : method.getOptions() ) {
				
				options.put( option.getName(), option.getValues().get( random.nextInt( option.getValues().size() ) ) );
				
			}
			
			DMActivityConfiguration activity = new DMActivityConfiguration( method.getName(), options );
			
			solution.addActivity( activity );
			
		}
		
		return solution;
	}

}
