package eu.supersede.gr.logics;

import java.util.List;

import org.springframework.stereotype.Service;

import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Service
public class Datastore {
	
	public interface DataJpaProvider {
		UsersJpa				getUsersJpa();
		RequirementsJpa			getRequirementsJpa();
		ValutationCriteriaJpa	getValutationCriteriaJpa();
	}
	
	private static final Datastore instance = new Datastore();
	
	public static final Datastore get() {
		return instance;
	}
	
//	@Autowired private RequirementsJpa			requirementsTable;
//	
//	@Autowired private ValutationCriteriaJpa	criteriaTable;
//	
//	@Autowired private UsersJpa					users;
	
	
	private DataJpaProvider jpaProvider;
	
	public void setDataJpaProvider( DataJpaProvider provider ) {
		this.jpaProvider = provider;
	}
	
	public void storeAsNew( Requirement r ) {
		
		r.setRequirementId(null);
		getRequirementsJpa().save(r);
		
	}
	
	
	public UsersJpa getUsersJpa() {
		return jpaProvider.getUsersJpa();
	}
	
	public RequirementsJpa getRequirementsJpa() {
		return jpaProvider.getRequirementsJpa();
	}
	
	public ValutationCriteriaJpa getValutationCriteriaJpa() {
		return jpaProvider.getValutationCriteriaJpa();
	}
	
	
	
	public List<User> listUsers() {
		return getUsersJpa().findAll();
	}
	
	public List<ValutationCriteria> listCriteria() {
		return getValutationCriteriaJpa().findAll();
	}
	
	public List<Requirement> listRequirements() {
		return getRequirementsJpa().findAll();
	}
	
}
