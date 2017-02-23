package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRole;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;

public class GAMethod implements DMMethod {
	
	public static final String NAME = "Genetic Algorithm based prioritization";
	
	static List<DMRoleSpec> roles = new ArrayList<>();
	
	static {
		roles.add( new DMRoleSpec( new DMRole( "Supervisor" ), 1, 1 ) );
		roles.add( new DMRoleSpec( new DMRole( "Player" ), 1, -1 ) );
		roles.add( new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) );
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
				if( mgr.getRequirementsCount() < 1 ) {
					return false;
				}
//				if( mgr.getOngoingActivities( getName() ).size() > 0 ) {
//					return false;
//				}
				// TODO: check that requirements status if "confirmed"
				return true;
			}} );
		return list;
	}
	
	@Override
	public String getPage( ProcessManager mgr ) {
		return "garp/home";
	}

}
