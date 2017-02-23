package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.DMTask;
import eu.supersede.dm.ProcessManager;

public class AHPPlayerMethod implements DMMethod {
	
	public static final String NAME = "Play AHP game session";
	
	String name;
	
	List<DMRoleSpec> list = new ArrayList<>();
	
	List<DMOption> options = new ArrayList<>();
	
	
	public AHPPlayerMethod() {
		this.name = NAME;
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

	public boolean isComplete( ProcessManager status ) {
		return getActiveTasks( status ).size() < 1;
	}

	public List<DMTask> getActiveTasks( ProcessManager status ) {
		return new ArrayList<>();
	}

	public void createGame( ProcessManager status ) {
		// TODO Auto-generated method stub

	}

	public void completeTask(ProcessManager status, DMTask task) {
//		executor.completeTask( status.getProperty( "pid", "" ), task );
	}

	@Override
	public List<DMCondition> preconditions() {
		List<DMCondition> list = new ArrayList<DMCondition>();
		list.add( new DMCondition() {
			@Override
			public boolean isTrue( ProcessManager mgr ) {
				return false;
//				return ( mgr.getOngoingActivities( getName() ).size() > 0 );
			}} );
		return list;
	}

	@Override
	public String getPage( ProcessManager mgr ) {
		return "ahprp/player_moves";
	}

}
