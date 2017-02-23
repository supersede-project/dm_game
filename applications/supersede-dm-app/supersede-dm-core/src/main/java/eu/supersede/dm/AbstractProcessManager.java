package eu.supersede.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.RequirementStatus;

public abstract class AbstractProcessManager implements ProcessManager {
	
	protected boolean isValidNextState( Integer cur, Integer nxt ) {
		RequirementStatus status = RequirementStatus.valueOf( cur );
		Set<RequirementStatus> nextSet = RequirementStatus.next( status );
		RequirementStatus next = RequirementStatus.valueOf( nxt );
		return nextSet.contains( next );
	}
	
	public List<ActivityEntry> findNextActivities( Collection<DMMethod> methods ) {
		
		List<ActivityEntry> list = new ArrayList<ActivityEntry>();
		
		for( DMMethod m : methods ) {
			boolean match = true;
			for( DMCondition cond : m.preconditions() ) {
				if( !cond.isTrue( this ) ) {
					match = false;
				}
			}
			if( match == true ) {
				// TODO: configure the activity entry
//				IDMGui gui = DMGuiManager.get().getGui( m.getName() );
//				if( gui == null ) {
//					continue;
//				}
				ActivityEntry ae = new ActivityEntry();
				ae.setMethodName( m.getName() );
				ae.setEntryUrl( m.getPage( this ) );
//				ae.setEntryUrl( gui.getEntryUrl() );
				list.add( ae );
			}
		}
		
		return list;
	}
	
	public HActivity createActivity( DMMethod method ) {
		return createActivity( method.getName() );
	}
}
