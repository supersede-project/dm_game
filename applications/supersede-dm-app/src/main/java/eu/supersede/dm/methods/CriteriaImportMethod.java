package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

public class CriteriaImportMethod implements DMMethod {
	
	public static final String NAME = "Import Criteria";
	
	
	String name;
	
	
	List<DMRoleSpec> list = new ArrayList<>();
	
	List<DMOption> options = new ArrayList<>();
	
	
	public CriteriaImportMethod() {
		
		this.name = NAME;
		
//		list.add( new DMRoleSpec( new DMRole( "Game Master" ), 1, 1 ) );
//		list.add( new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) );
//		list.add( new DMRoleSpec( new DMRole( "Opinion Provider" ), 1, -1 ) );
//		
//		options.add( new DMOption( "gamification", new String[] { "on", "off" } ) );
//		options.add( new DMOption( "negotiator", new String[] { "active", "not active" } ) );
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public DMObjective getObjective() {
		return DMObjective.PrioritizeRequirements;
	}
	
	public List<DMRoleSpec> getRoleList() {
		return list;
	}

	public String getPage( String step ) {
		return "";
	}

	@Override
	public List<DMOption> getOptions() {
		return this.options;
	}

	public void setOption( String optName, String optValue ) {
		// TODO
	}

	public void init( ProcessManager status ) {
//		String pid = executor.startBPMN( "supersedeAHPDM" );
//		status.setProperty( "pid", pid );
	}

	@Override
	public List<DMCondition> preconditions() {
		List<DMCondition> list = new ArrayList<DMCondition>();
		list.add( new DMCondition() {
			@Override
			public boolean isTrue( ProcessManager mgr ) {
				for( Requirement r : mgr.requirements() ) {
					if( r.getStatus() != RequirementStatus.Unconfirmed.getValue() ) {
						return false;
					}
				}
				return true;
			}} );
		return list;
	}
	
	@Override
	public String getPage( ProcessManager mgr ) {
		return "import_criteria";
	}

}
