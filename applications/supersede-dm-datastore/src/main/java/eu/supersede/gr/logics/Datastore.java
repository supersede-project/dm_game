package eu.supersede.gr.logics;

import org.springframework.beans.factory.annotation.Autowired;

import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.Requirement;

public class Datastore {
	
	private static final Datastore instance = new Datastore();
	
	public static final Datastore get() {
		return instance;
	}
	
	@Autowired
    private RequirementsJpa requirementsTable;
	
	public void storeAsNew( Requirement r ) {
		
		r.setRequirementId(null);
		requirementsTable.save(r);
		
	}
	
}
