package eu.supersede.gr.logics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Service
public class Datastore
{
    @Autowired
    private RequirementsJpa requirements;

    @Autowired
    private ValutationCriteriaJpa criteriaTable;

    @Autowired
    private UsersJpa users;

    public void storeAsNew(Requirement r)
    {
        r.setRequirementId(null);
        getRequirementsJpa().save(r);
    }

    public UsersJpa getUsersJpa()
    {
        return users;
    }

    public RequirementsJpa getRequirementsJpa()
    {
        return requirements;
    }

    public ValutationCriteriaJpa getValutationCriteriaJpa()
    {
        return criteriaTable;
    }

    public List<User> listUsers()
    {
        return getUsersJpa().findAll();
    }

    public List<ValutationCriteria> listCriteria()
    {
        return getValutationCriteriaJpa().findAll();
    }

    public List<Requirement> listRequirements()
    {
        return getRequirementsJpa().findAll();
    }
}