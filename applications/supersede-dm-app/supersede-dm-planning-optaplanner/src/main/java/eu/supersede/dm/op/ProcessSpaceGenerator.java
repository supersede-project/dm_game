package eu.supersede.dm.op;

public class ProcessSpaceGenerator {
	
	public ProcessChain createProcessPlan() {
		
		ProcessChain plan = new ProcessChain();
		
		plan.setObjective( "g.a.-based prioritization" );
		
		plan.getActivities().add( new ActivityExecution( "start" ) );
		plan.getActivities().add( new ActivityExecution( "enact" ) );
		plan.getActivities().add( new ActivityExecution( "g.a.-based prioritization" ) );
		plan.getActivities().add( new ActivityExecution( "ahp-based prioritization" ) );
		
		return plan;
	}
	
}
