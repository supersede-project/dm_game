package eu.supersede.dm;

import java.util.List;
import java.util.Set;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

public class PersistedProcess implements ProcessManager {
	
	Long processId;
	
	
	public PersistedProcess(Long processId) {
		this.processId = processId;
	}

	@Override
	public void addRequirement(Requirement r) {
		r.setProcessId( processId );
		DMGame.get().jpaRequirements.save( r );
	}

	@Override
	public List<Requirement> requirements() {
		return DMGame.get().jpaRequirements.findRequirementsByProcessId( processId );
	}

	@Override
	public int getRequirementsCount() {
		return requirements().size();
	}

	@Override
	public void setRequirementsStatus(List<Requirement> reqs, Integer status) {
		for( Requirement r : reqs ) {
			if( isValidNextState( r.getStatus(), status ) ) {
				r.setStatus( status );
				DMGame.get().jpaRequirements.save( r );
			}
		}
	}
	
	private boolean isValidNextState( Integer cur, Integer nxt ) {
		RequirementStatus status = RequirementStatus.valueOf( cur );
		Set<RequirementStatus> nextSet = RequirementStatus.next( status );
		RequirementStatus next = RequirementStatus.valueOf( nxt );
		return nextSet.contains( next );
	}
	
	@Override
	public Long addProcessMember(Long userId, String role) {
		HProcessMember m = new HProcessMember();
		m.setProcessId( processId );
		m.setUserId( userId );
		m.setRole( role );
		m = DMGame.get().jpaMembers.save( m );
		return m.getId();
	}

	@Override
	public List<HProcessMember> getProcessMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HProcessMember> getProcessMembers( String role ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HActivity createActivity( DMMethod method ) {
		HActivity a = new HActivity();
		a.setProcessId( processId );
		a.setMethodName( method.getName() );
		return DMGame.get().jpaActivities.save( a );
	}

}
