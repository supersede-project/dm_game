package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRequirement;
import eu.supersede.dm.DMRole;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.DMStatus;
import eu.supersede.dm.DMTask;
import eu.supersede.dm.DMTopic;
import eu.supersede.dm.ahp.algorithm.AHPStructure;
import eu.supersede.dm.ahp.algorithm.Ahp;
import eu.supersede.dm.exec.BPMNExecutor;

public class AHPRequirementsPrioritizationMethod implements DMMethod {
	
	public static final String NAME = "AHP session";
	
	
	String name;
	
	
	BPMNExecutor executor = new BPMNExecutor();
	
	
	List<DMRoleSpec> list = new ArrayList<>();
	
	List<DMOption> options = new ArrayList<>();
	
	
	public AHPRequirementsPrioritizationMethod() {

		this.name = NAME;

		list.add( new DMRoleSpec( new DMRole( "Game Master" ), 1, 1 ) );
		list.add( new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) );
		list.add( new DMRoleSpec( new DMRole( "Opinion Provider" ), 1, -1 ) );

		options.add( new DMOption( "gamification", new String[] { "on", "off" } ) );
		options.add( new DMOption( "negotiator", new String[] { "active", "not active" } ) );

		executor.loadBPMN( "supersedeAHPDM.bpmn20.xml" );
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

	//	@Override
	//	public DMProcess createInstance(DMActivity activity) {
	//		return new AHPRPProcess();
	//	}

	//	public DMStatus init( DMActivity activity ) {
	//		String pid = executor.startBPMN( "supersedeAHPDM" );
	//		DMStatus status = new DMStatus();
	//		status.setProperty( "pid", pid );
	//		return status;
	//	}

	public void init( DMStatus status ) {
		String pid = executor.startBPMN( "supersedeAHPDM" );
		status.setProperty( "pid", pid );
	}

	public void callAHP() {

		AHPStructure objAHP = new AHPStructure();

		//Preference from 0 to 8
		objAHP.setCriteria("C1","C2");
		objAHP.setOptions("Op1", "Op2" );

		//Preference from 0 to 8
		objAHP.setPreference("C1", "C2", 6);
		objAHP.setOptionPreference("Op1", "Op2", "C1", 2);
		objAHP.setOptionPreference("Op1", "Op2", "C2", 8);

		Ahp objCalculateRank = new Ahp(objAHP);

		objCalculateRank.execute();
	}

	public boolean isComplete( DMStatus status ) {
		return getActiveTasks( status ).size() < 1;
		//		return executor.isProcessComplete( status.getProperty( "pid", "" ) );
	}

	public List<DMTask> getActiveTasks( DMStatus status ) {
		return executor.getActiveTasks( status.getProperty( "pid", "" ) );
	}

	public void createGame( DMStatus status ) {
		// TODO Auto-generated method stub

	}

	public void completeTask(DMStatus status, DMTask task) {
		executor.completeTask( status.getProperty( "pid", "" ), task );
	}

	public void callAHP( DMStatus status ) {

		AHPStructure objAHP = new AHPStructure();

		Set<DMTopic> topics = new HashSet<>();
		for( DMRequirement r : status.requirements() ) {
			if( r.getTopic() != DMTopic.none ) {
				if( topics.contains( r.getTopic() ) ) {
					topics.add( r.getTopic() );
				}
			}
		}
		
		if( topics.size() < 1 ) {
			topics.add( new DMTopic( "Priority" ) );
		}
		
		{
			List<String> tlist = new ArrayList<>();
			for( DMTopic topic : topics ) {
				tlist.add( topic.getName() );
			}
			objAHP.setCriteria( tlist );
		}
		
		{
			List<String> reqs = new ArrayList<>();
			for( DMRequirement r : status.requirements() ) {
				reqs.add( r.getId() );
			}
			objAHP.setOptions( reqs );
		}

		Random random = new Random( System.currentTimeMillis() );
		
		for( DMTopic t1 : topics ) {
			for( DMTopic t2 : topics ) {
				//Preference from 0 to 8
				objAHP.setPreference( t1.getName(), t2.getName(), random.nextInt( 9 ) );
			}
		}
		
		for( DMTopic topic : topics ) {
			for( DMRequirement r1 : status.requirements() ) {
				for( DMRequirement r2 : status.requirements() ) {
					//Preference from 0 to 8
					objAHP.setOptionPreference( r1.getId(), r2.getId(), topic.getName(), random.nextInt( 9 ) );
				}
			}
		}
		
		Ahp objCalculateRank = new Ahp(objAHP);

		Map<String, Double> result = objCalculateRank.execute();
		
		System.out.println( "Results:" );
		for( String key : result.keySet() ) {
			
			System.out.println( key + " = " + result.get( key ) );
			
		}
		System.out.println( "SENDINGG RESULTS AHEAD FOR ENACTMENT" );
	}

}
