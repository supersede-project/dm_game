package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.HPropertyBag;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

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
	public HActivity createActivity( String methodName, Long userId ) {
		HActivity a = new HActivity();
		a.setProcessId( processId );
		a.setUserId( userId );
		a.setMethodName( methodName );
		return DMGame.get().jpa.activities.save( a );
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
		return new ArrayList<>();
	}

	@Override
	public void addCriterion( ValutationCriteria vc ) {
		HProcessCriterion c = new HProcessCriterion();
		c.setSourceId( vc.getCriteriaId() );
		c.setDescription( vc.getDescription() );
		c.setProcessId( this.processId );
		c.setName( vc.getName() );
		DMGame.get().jpa.processCriteria.save( c );
	}

	@Override
	public List<ValutationCriteria> getCrtiteria() {
		List<ValutationCriteria> list = new ArrayList<>();
		List<HProcessCriterion> procList = DMGame.get().jpa.processCriteria.findProcessCriteria( this.processId );
		for( HProcessCriterion pc : procList ) {
			ValutationCriteria v = new ValutationCriteria();
			v.setCriteriaId( pc.getSourceId() );
			v.setDescription( pc.getDescription() );
			v.setName( pc.getName() );
			v.setUserCriteriaPoints( new ArrayList<>() );
			list.add( v );
		}
		return list;
	}

	@Override
	public List<HProcessCriterion> getProcessCrtiteria() {
		return DMGame.get().jpa.processCriteria.findProcessCriteria( this.processId );
	}

	@Override
	public int getCriteriaCount() {
		return DMGame.get().jpa.processCriteria.findProcessCriteria( this.processId ).size();
	}

	@Override
	public List<HActivity> getOngoingActivities( String methodName ) {
		return DMGame.get().jpa.activities.find( this.processId, methodName );
	}

	@Override
	public PropertyBag getProperties(HActivity a) {
		HPropertyBag bag = null;
		if( a.getPropertyBag() != null ) {
			bag = DMGame.get().jpa.propertyBags.findOne( a.getPropertyBag() );
		}
		if( bag == null ) {
			bag = new HPropertyBag();
			bag = DMGame.get().jpa.propertyBags.save( bag );
			a.setPropertyBag( bag.getId() );
			a = DMGame.get().jpa.activities.save( a );
		}
		return new PropertyBag( a );
	}

}
