package eu.supersede.dm;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.ProcessMembersJpa;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HProcess;

@Component
public class DMGame {
	
	private static final DMGame instance = new DMGame();
	
	public static DMGame get() {
		return instance;
	}
		

	@Autowired UsersJpa					jpaUsers;
	@Autowired RequirementsJpa			jpaRequirements;
    @Autowired ValutationCriteriaJpa	jpaCriteria;
    @Autowired ProcessesJpa				jpaProcesses;
    @Autowired ProcessMembersJpa		jpaMembers;
	@Autowired ActivitiesJpa			jpaActivities;
    
    
	public HProcess createEmptyProcess() {
    	HProcess proc = new HProcess();
    	proc.setObjective( DMObjective.PrioritizeRequirements.name() );
    	proc.setStartTime( new Date( System.currentTimeMillis() ) );
    	proc = jpaProcesses.save( proc );
    	return proc;
    }
	
	public HProcess getProcess( Long processId ) {
		return jpaProcesses.findOne( processId );
	}
	
	public PersistedProcess getProcessStatus( Long processId ) {
		return new PersistedProcess( processId );
	}
	
}
