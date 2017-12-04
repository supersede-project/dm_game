package eu.supersede.dm.op;

import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.IDMPlanner;

public class DMOptaPlanner implements IDMPlanner {

	private DMRuleset ruleset;

	public DMOptaPlanner(DMRuleset ruleset) {
		this.ruleset = ruleset;
	}

	@Override
	public DMSolution solve( DMProblem problem ) {
		
		OPOrchestrator orchestrator = new OPOrchestrator( problem, ruleset );
		
        TimeDiff diff = new TimeDiff();
        
		ProcessChain process = orchestrator.plan();
		
        diff.set();
        
        System.out.println( "Reporting..." );
        
        System.out.println( diff );
        System.out.println( process.getProcessPath() );
		System.out.println( "Score: " + process.getScore().getHardScore() + "; " + process.getScore().getSoftScore() );
		
		return orchestrator.getSolution();
	}

}
