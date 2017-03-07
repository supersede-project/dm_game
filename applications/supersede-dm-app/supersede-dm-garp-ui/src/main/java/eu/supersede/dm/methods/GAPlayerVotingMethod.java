package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;

public class GAPlayerVotingMethod implements DMMethod {
	
	public static final String NAME = "Vote in a prioritization task";
	
	static List<DMRoleSpec> roles = new ArrayList<>();
	
	static {
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DMObjective getObjective() {
		return DMObjective.PrioritizeRequirements;
	}

	@Override
	public List<DMOption> getOptions() {
		return new ArrayList<>();
	}

	@Override
	public List<DMRoleSpec> getRoleList() {
		return roles;
	}
	
	@Override
	public List<DMCondition> preconditions() {
		List<DMCondition> list = new ArrayList<DMCondition>();
		list.add( new DMCondition() {
			@Override
			public boolean isTrue( ProcessManager mgr ) {
				return false;
			}} );
		return list;
	}
	
	@Override
	public String getPage( ProcessManager mgr ) {
		return "garp/vote";
	}

}
