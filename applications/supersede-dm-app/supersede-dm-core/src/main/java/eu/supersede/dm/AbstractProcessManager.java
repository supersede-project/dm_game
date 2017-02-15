package eu.supersede.dm;

import java.util.Set;

import eu.supersede.gr.model.RequirementStatus;

public abstract class AbstractProcessManager implements ProcessManager {
	
	protected boolean isValidNextState( Integer cur, Integer nxt ) {
		RequirementStatus status = RequirementStatus.valueOf( cur );
		Set<RequirementStatus> nextSet = RequirementStatus.next( status );
		RequirementStatus next = RequirementStatus.valueOf( nxt );
		return nextSet.contains( next );
	}
	
}
