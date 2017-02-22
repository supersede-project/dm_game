package eu.supersede.dm;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.AlertsJpa;
import eu.supersede.gr.jpa.AppsJpa;
import eu.supersede.gr.jpa.ProcessCriteriaJpa;
import eu.supersede.gr.jpa.ProcessMembersJpa;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.ReceivedUserRequestsJpa;
import eu.supersede.gr.jpa.RequirementsDependenciesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.ValutationCriteria;

@Component
public class DMGame {
	
	private static DMGame instance = null;
	
	public static void init( JpaProvider jpa ) {
		if( instance == null ) {
			instance = new DMGame();
			instance.jpa = jpa;
		}
	}
	
	public static DMGame get() {
		return instance;
	}
	
	public static class JpaProvider {
		public UsersJpa						users;
		public RequirementsJpa				requirements;
		public ValutationCriteriaJpa		criteria;
		public ProcessesJpa					processes;
		public ProcessMembersJpa			members;
		public ActivitiesJpa				activities;
		public ProcessCriteriaJpa			processCriteria;
		public AlertsJpa					alerts;
		public AppsJpa						apps;
		public ReceivedUserRequestsJpa		receivedUserRequests;
		public RequirementsPropertiesJpa	requirementProperties;
		public RequirementsDependenciesJpa	requirementDependencies;
	}
	
	JpaProvider jpa;
	
    
    
	public HProcess createEmptyProcess() {
    	HProcess proc = new HProcess();
    	proc.setObjective( DMObjective.PrioritizeRequirements.name() );
    	proc.setStartTime( new Date( System.currentTimeMillis() ) );
    	proc = jpa.processes.save( proc );
    	proc.setName( "Process " + proc.getId() );
    	proc = jpa.processes.save( proc );
    	return proc;
    }
	
	public HProcess getProcess( Long processId ) {
		return jpa.processes.findOne( processId );
	}
	
	public PersistedProcess getProcessStatus( Long processId ) {
		return new PersistedProcess( processId );
	}
	
	public List<ValutationCriteria> getCriteria() {
		return this.jpa.criteria.findAll();
	}
	
	public ValutationCriteria getCriterion( Long id ) {
		return this.jpa.criteria.findOne( id );
	}
	
	public JpaProvider getJpa() {
		return this.jpa;
	}
	
}
