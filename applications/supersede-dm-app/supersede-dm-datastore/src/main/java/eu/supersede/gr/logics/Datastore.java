/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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