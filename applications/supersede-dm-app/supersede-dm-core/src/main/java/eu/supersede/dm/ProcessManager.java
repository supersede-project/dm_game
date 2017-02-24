package eu.supersede.dm;

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

public interface ProcessManager {
	
	public void addRequirement( Requirement r );
	public List<Requirement> requirements();
	public int getRequirementsCount();
	public void setRequirementsStatus( List<Requirement> reqs, Integer status );
	
	
	public void addCriterion( ValutationCriteria criterion );
	public List<ValutationCriteria> getCrtiteria();
	public int getCriteriaCount();
	
	public void addAlert( HAlert alert );
	public List<HAlert> getAlerts();
	
	
	public Long addProcessMember( Long userId, String role );
	public List<HProcessMember> getProcessMembers();
	public List<HProcessMember> getProcessMembers( String role );
	
	
//	public HActivity createActivity( DMMethod method );
//	public HActivity createActivity( String methodName );
	public HActivity createActivity( String methodName, Long userId );
	public List<HActivity> getOngoingActivities();
	public List<HActivity> getOngoingActivities( String methodName );
	
	public PropertyBag getProperties( HActivity a );
	
}
