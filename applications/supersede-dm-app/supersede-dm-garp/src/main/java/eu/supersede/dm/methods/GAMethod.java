package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRole;
import eu.supersede.dm.DMRoleSpec;

public class GAMethod implements DMMethod {
	
	static List<DMRoleSpec> roles = new ArrayList<>();
	
	static {
		roles.add( new DMRoleSpec( new DMRole( "Supervisor" ), 1, 1 ) );
		roles.add( new DMRoleSpec( new DMRole( "Player" ), 1, -1 ) );
		roles.add( new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) );
	}
	
	@Override
	public String getName() {
		return "Genetic Algorithm based prioritization";
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

}
