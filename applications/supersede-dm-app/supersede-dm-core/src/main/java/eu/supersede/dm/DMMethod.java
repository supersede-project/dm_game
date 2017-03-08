package eu.supersede.dm;

import java.util.List;
import java.util.Map;

public interface DMMethod {
	
	public String				getName();
	
	public DMObjective			getObjective();
	
	public List<DMRoleSpec>		getRoleList();
	
	public List<DMOption>		getOptions();
	
	public List<DMCondition>	preconditions();
	
	public String				getPage( ProcessManager mgr );
	
//	public String				post( String action, Map<String,String> args );

}
