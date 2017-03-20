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

package eu.supersede.dm;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.AlertsJpa;
import eu.supersede.gr.jpa.AppsJpa;
import eu.supersede.gr.jpa.ProcessCriteriaJpa;
import eu.supersede.gr.jpa.ProcessMembersJpa;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.PropertiesJpa;
import eu.supersede.gr.jpa.PropertyBagsJpa;
import eu.supersede.gr.jpa.ReceivedUserRequestsJpa;
import eu.supersede.gr.jpa.RequirementsDependenciesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.jpa.RequirementsRankingsJpa;
import eu.supersede.gr.jpa.RequirementsScoresJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Component
public class DMGame
{
    private static DMGame instance = null;

    public static void init(JpaProvider jpa)
    {
        if (instance == null)
        {
            instance = new DMGame();
            instance.jpa = jpa;
        }
    }

    public static DMGame get()
    {
        return instance;
    }

    public static class JpaProvider
    {
        public UsersJpa users;
        public RequirementsJpa requirements;
        public ValutationCriteriaJpa criteria;
        public ProcessesJpa processes;
        public ProcessMembersJpa members;
        public ActivitiesJpa activities;
        public ProcessCriteriaJpa processCriteria;
        public AlertsJpa alerts;
        public AppsJpa apps;
        public ReceivedUserRequestsJpa receivedUserRequests;
        public RequirementsPropertiesJpa requirementProperties;
        public RequirementsDependenciesJpa requirementDependencies;
        public PropertiesJpa properties;
        public PropertyBagsJpa propertyBags;
        public RequirementsRankingsJpa requirementsRankings;
        public RequirementsScoresJpa scoresJpa;
    }

    JpaProvider jpa;

    DMLifecycle lifecycle = new DMLifecycle();

    private DMGame()
    {
        lifecycle.addInitialPhase(new DMPhase("Initialization")
        {
            @Override
            public void activate(ProcessManager mgr)
            {
                for (Requirement r : mgr.getRequirements())
                {
                    r.setStatus(RequirementStatus.Unconfirmed.getValue());
                    DMGame.get().getJpa().requirements.save(r);
                }
            }
        });
        lifecycle.addPhase(new DMPhase("Editing")
        {
            @Override
            public void checkPreconditions(ProcessManager mgr) throws Exception
            {

                if (mgr.getRequirementsCount() == 0)
                {
                    throw new Exception("At least one requirement must exist in the process");
                }

                DuplicateMap<Integer, Requirement> map = new DuplicateMap<>();

                for (Requirement r : mgr.getRequirements())
                {
                    map.put(r.getStatus(), r);
                }

                if (map.count(RequirementStatus.Discarded.getValue()) == map.size())
                {
                    throw new Exception("No requirements can be edited (all of them are discarded)");
                }

                if (map.count(RequirementStatus.Enacted.getValue()) > 0)
                {
                    throw new Exception("There are already enacted requirements");
                }

                if ((map.count(RequirementStatus.Unconfirmed.getValue()) > 0)
                        & (map.count(RequirementStatus.Confirmed.getValue()) > 0))
                {
                    throw new Exception(
                            "Not a valid state: there are both unconfirmed and already confirmed requirements");
                }
            }

            @Override
            public void activate(ProcessManager mgr)
            {
                for (Requirement r : mgr.getRequirements())
                {
                    r.setStatus(RequirementStatus.Editable.getValue());
                    DMGame.get().getJpa().requirements.save(r);
                }
            }
        });
        lifecycle.addPhase(new DMPhase("Prioritization")
        {
            @Override
            public void checkPreconditions(ProcessManager mgr) throws Exception
            {
                if (mgr.getRequirementsCount() == 0)
                {
                    throw new Exception("At least one requirement must exist in the process");
                }

                DuplicateMap<Integer, Requirement> map = new DuplicateMap<>();

                for (Requirement r : mgr.getRequirements())
                {
                    map.put(r.getStatus(), r);
                }

                if (map.count(RequirementStatus.Discarded.getValue()) == map.size())
                {
                    throw new Exception("No requirements can be edited (all of them are discarded)");
                }

                if (map.count(RequirementStatus.Enacted.getValue()) > 0)
                {
                    throw new Exception("There are already enacted requirements");
                }

                if ((map.count(RequirementStatus.Unconfirmed.getValue()) > 0))
                {
                    throw new Exception("Not a valid state: there are still unconfirmed requirements");
                }
            }

            @Override
            public void activate(ProcessManager mgr)
            {
                for (Requirement r : mgr.getRequirements())
                {
                    r.setStatus(RequirementStatus.Confirmed.getValue());
                    DMGame.get().getJpa().requirements.save(r);
                }
            }
        });
        lifecycle.addPhase(new DMPhase("Terminated")
        {
            @Override
            public void checkPreconditions(ProcessManager mgr) throws Exception
            {
                if (mgr.getRequirementsCount() == 0)
                {
                    throw new Exception("At least one requirement must exist in the process");
                }

                DuplicateMap<Integer, Requirement> map = new DuplicateMap<>();

                for (Requirement r : mgr.getRequirements())
                {
                    map.put(r.getStatus(), r);
                }

                if (map.count(RequirementStatus.Discarded.getValue()) == map.size())
                {
                    throw new Exception("No requirements can be edited (all of them are discarded)");
                }

                if (map.count(RequirementStatus.Confirmed.getValue()) < map.size())
                {
                    throw new Exception("Not all the requirements are confirmed");
                }
            }
        });

        lifecycle.setNext("Initialization", "Editing");
        lifecycle.setNext("Editing", "Prioritization");
        lifecycle.setNext("Prioritization", "Terminated");
    }

    public DMLifecycle getLifecycle()
    {
        return this.lifecycle;
    }

    public HProcess createEmptyProcess(String name)
    {
        HProcess proc = new HProcess();
        proc.setObjective(DMObjective.PrioritizeRequirements.name());
        proc.setStartTime(new Date(System.currentTimeMillis()));
        proc.setPhaseName(lifecycle.getInitPhase().getName());
        proc = jpa.processes.save(proc);

        if (name == null)
        {
            proc.setName("Process " + proc.getId());
        }
        else
        {
            proc.setName(name);
        }

        proc = jpa.processes.save(proc);
        return proc;
    }

    public void deleteProcess(Long processId)
    {
        ProcessManager mgr = getProcessManager(processId);
        List<HActivity> activities = mgr.getOngoingActivities();

        if (activities != null && activities.size() > 0)
        {
            throw new InternalServerErrorException(
                    "This process contains ongoing activities. To close it, you must close the activities first.");
        }

        for (Requirement r : mgr.getRequirements())
        {
            mgr.removeRequirement(r.getRequirementId());
        }

        for (HProcessMember m : mgr.getProcessMembers())
        {
            mgr.removeProcessMember(m.getId(), m.getUserId(), m.getProcessId());
        }

        for (HProcessCriterion c : mgr.getProcessCriteria())
        {
            mgr.removeCriterion(c.getCriterionId(), c.getSourceId(), c.getProcessId());
        }

        jpa.processes.delete(processId);
        // TODO: let all methods cleanup their stuff (e.g. games) in the db, if necessary
    }

    public HProcess getProcess(Long processId)
    {
        HProcess process = jpa.processes.findOne(processId);

        if (process == null)
        {
            throw new NotFoundException("Can't find process with id: " + processId);
        }

        return process;
    }

    public PersistedProcess getProcessManager(Long processId)
    {
        return new PersistedProcess(processId);
    }

    public List<ValutationCriteria> getCriteria()
    {
        return this.jpa.criteria.findAll();
    }

    public ValutationCriteria getCriterion(Long id)
    {
        ValutationCriteria criterion = jpa.criteria.findOne(id);

        if (criterion == null)
        {
            throw new NotFoundException("Criterion with id " + id + " not found!");
        }

        return criterion;
    }

    public JpaProvider getJpa()
    {
        return jpa;
    }

    public List<User> getCandidateProcessUsers()
    {
        return jpa.users.findAll();
    }

    public List<HActivity> getPendingActivities(Long userId)
    {
        return jpa.activities.findByUser(userId);
    }
}