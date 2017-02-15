package eu.supersede.dm;

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;

public class PersistedProcess extends AbstractProcessManager {
	
	Long processId;
	
	
	public PersistedProcess(Long processId) {
		this.processId = processId;
	}

	@Override
	public void addRequirement(Requirement r) {
		r.setProcessId( processId );
		DMGame.get().jpa.requirements.save( r );
	}

	@Override
	public List<Requirement> requirements() {
		return DMGame.get().jpa.requirements.findRequirementsByProcessId( processId );
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
				DMGame.get().jpa.requirements.save( r );
			}
		}
	}
	
	@Override
	public Long addProcessMember(Long userId, String role) {
		HProcessMember m = new HProcessMember();
		m.setProcessId( processId );
		m.setUserId( userId );
		m.setRole( role );
		m = DMGame.get().jpa.members.save( m );
		return m.getId();
	}

	@Override
	public List<HProcessMember> getProcessMembers() {
		return DMGame.get().jpa.members.findProcessMembers( processId );
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
		return DMGame.get().jpa.activities.save( a );
	}

	@Override
	public void addAlert(HAlert alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<HAlert> getAlerts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HActivity> getOngoingActivities() {
		// TODO Auto-generated method stub
		return null;
	}

}
