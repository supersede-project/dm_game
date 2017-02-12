package eu.supersede.dm;

import java.util.List;

public interface DMMethod {
	
	public String				getName();
	
	public DMObjective			getObjective();
	
	public List<DMRoleSpec>		getRoleList();
	
	public List<DMOption>		getOptions();
	
	public List<DMCondition>	preconditions();

}
