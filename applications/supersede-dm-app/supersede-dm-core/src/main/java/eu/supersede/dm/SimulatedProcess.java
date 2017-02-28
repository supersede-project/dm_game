package eu.supersede.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

public class SimulatedProcess extends AbstractProcessManager {
	
	private static Long counter = 0L;
	
	Long processId;
	
	List<Requirement> requirements = new ArrayList<>();
	
	Map<Long,HProcess> processes = new HashMap<>();
	
	Map<Long,List<HProcessMember>> members = new HashMap<>();
	
	Map<Long,List<HActivity>> activities = new HashMap<>();
	
	
	public SimulatedProcess( Long processId ) {
		this.processId = processId;
	}
	
	@Override
	public void addRequirement( Requirement req ) {
		requirements.add( req );
	}

	@Override
	public int getRequirementsCount() {
		return requirements.size();
	}

	@Override
	public List<Requirement> requirements() {
		return requirements;
	}

	@Override
	public void setRequirementsStatus(List<Requirement> reqs, Integer status) {
		for( Requirement r : reqs ) {
			if( isValidNextState( r.getStatus(), status ) ) {
				r.setStatus( status );
			}
		}
	}
	
	@Override
	public Long addProcessMember( Long userId, String role) {
		List<HProcessMember> list = members.get( processId );
		if( list == null ) {
			list = new ArrayList<>();
			members.put( processId, list );
		}
		HProcessMember m = new HProcessMember();
		m.setId( (++counter) );
		m.setProcessId( processId );
		m.setUserId( userId );
		m.setRole( role );
		list.add( m );
		return m.getId();
	}

	@Override
	public List<HProcessMember> getProcessMembers() {
		return members.get( processId );
	}

	@Override
	public List<HProcessMember> getProcessMembers(String role) {
		List<HProcessMember> list = members.get( processId );
		if( list == null ) {
			return null;
		}
		List<HProcessMember> ret = new ArrayList<>();
		for( HProcessMember m : list ) {
			if( role.equals( m.getRole() ) ) {
				ret.add( m );
			}
		}
		return ret;
	}

	@Override
	public HActivity createActivity( String methodName, Long userId ) {
		List<HActivity> list = activities.get( processId );
		if( list == null ) {
			list = new ArrayList<>();
			activities.put( processId, list );
		}
		HActivity a = new HActivity();
		a.setId( (++counter) );
		a.setProcessId( processId );
		a.setUserId( userId );
		a.setMethodName( methodName );
		list.add( a );
		return a;
	}

	@Override
	public void addAlert(HAlert alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<HAlert> getAlerts() {
		return new ArrayList<>();
	}

	@Override
	public List<HActivity> getOngoingActivities() {
		List<HActivity> list = activities.get( processId );
		if( list == null ) {
			list = new ArrayList<>();
		}
		return list;
	}

	@Override
	public void addCriterion(ValutationCriteria criterion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ValutationCriteria> getCrtiteria() {
		return new ArrayList<>();
	}

	@Override
	public int getCriteriaCount() {
		return 0;
	}

	@Override
	public List<HActivity> getOngoingActivities(String methodName) {
		List<HActivity> all = getOngoingActivities();
		List<HActivity> sel = new ArrayList<>();
		for( HActivity a : all ) {
			if( a.getMethodName().equals( methodName ) ) {
				sel.add( a );
			}
		}
		return sel;
	}

	@Override
	public PropertyBag getProperties(HActivity a) {
		return new PropertyBag();
	}

	@Override
	public List<HProcessCriterion> getProcessCrtiteria() {
		return new ArrayList<>();
	}

}
