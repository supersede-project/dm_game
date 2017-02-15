package eu.supersede.dm;

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;

public interface ProcessManager {
	
	public void addRequirement( Requirement r );
	
	public List<Requirement> requirements();
	
	public int getRequirementsCount();
	
	public void setRequirementsStatus( List<Requirement> reqs, Integer status );
	
	
	public Long addProcessMember( Long userId, String role );
	
	public List<HProcessMember> getProcessMembers();
	public List<HProcessMember> getProcessMembers(String role );
	
	
	public HActivity createActivity( DMMethod method );
	
	
}
