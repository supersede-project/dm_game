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

package eu.supersede.dm.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.methods.AccessRequirementsEditingSession;
import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.RequirementsDependenciesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.HRequirementDependency;
import eu.supersede.gr.model.HRequirementProperty;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

@RestController
@RequestMapping("processes/requirements")
public class ProcessRequirementsRest
{
    @Autowired
    private RequirementsJpa requirementsJpa;

    @Autowired
    private RequirementsDependenciesJpa requirementsDependenciesJpa;

    @Autowired
    private RequirementsPropertiesJpa requirementsPropertiesJpa;

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Requirement createRequirement(@RequestParam Long processId, @RequestParam String name)
    {
        Requirement r = new Requirement();
        r.setName(name);
        r = DMGame.get().getJpa().requirements.save(r);
        DMGame.get().getProcessManager(processId).addRequirement(r);
        return r;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteRequirement(@RequestParam Long requirementId)
    {
        Requirement requirement = requirementsJpa.getOne(requirementId);

        if (requirement == null)
        {
            throw new NotFoundException(
                    "Can't delete requirement with id " + requirementId + " because it does not exist");
        }

        // Do not delete the requirement if at least another requirement depends on it
        List<HRequirementDependency> dependencies = requirementsDependenciesJpa.findByDependencyId(requirementId);

        if (dependencies != null && dependencies.size() > 0)
        {
            throw new InternalServerErrorException("Unable to delete requirement " + requirement.getName() + "("
                    + requirement.getDescription() + "): at least another requirement depends on it");
        }

        // Delete all the dependencies of the requirement
        dependencies = requirementsDependenciesJpa.findByRequirementId(requirementId);

        for (HRequirementDependency requirementDependency : dependencies)
        {
            requirementsDependenciesJpa.delete(requirementDependency);
        }

        // Delete all the properties of the requirement
        for (HRequirementProperty requirementProperty : requirementsPropertiesJpa
                .findPropertiesByRequirementId(requirementId))
        {
            requirementsPropertiesJpa.delete(requirementProperty);
        }

        requirementsJpa.delete(requirement);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void importRequirements(@RequestParam Long processId,
            @RequestParam(required = false) List<Long> requirementsId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        List<Requirement> requirements = proc.getRequirements();

        for (Requirement requirement : requirements)
        {
            proc.removeRequirement(requirement.getRequirementId());
        }

        if (requirementsId == null)
        {
            // No requirement has been added to the process
            return;
        }

        for (Long requirementId : requirementsId)
        {
            Requirement requirement = DMGame.get().getJpa().requirements.findOne(requirementId);

            if (requirement == null)
            {
                throw new NotFoundException(
                        "Can't add requirement with id " + requirementId + " to the process because it does not exist");
            }

            proc.addRequirement(requirement);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Requirement> getRequirementsList(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getRequirements();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Requirement getRequirement(@RequestParam Long processId, @RequestParam Long reqId)
    {
        return DMGame.get().getProcessManager(processId).getRequirement(reqId);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public int getRequirementsCount(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getRequirementsCount();
    }

    // Checks if a certain status pertains ALL the requirements
    @RequestMapping(value = "/stablestatus", method = RequestMethod.GET, produces = "text/plain")
    public String getRequirementsStableStatus(@RequestParam Long processId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        Map<String, Integer> count = new HashMap<>();
        List<Requirement> requirements = proc.getRequirements();

        for (Requirement r : requirements)
        {
            RequirementStatus s = RequirementStatus.valueOf(r.getStatus());
            Integer n = count.get(r.getStatus());

            if (n == null)
            {
                n = 0;
            }

            count.put(s.name(), n + 1);
        }

        if (count.size() != 1)
        {
            return "";
        }

        return count.keySet().toArray()[0].toString();
    }

    // Sets a same status to ALL the requirements
    @RequestMapping(value = "/stablestatus", method = RequestMethod.POST, produces = "text/plain")
    public void setRequirementsStableStatus(@RequestParam Long processId,
            @RequestParam(name = "status") String statusString)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        RequirementStatus status = RequirementStatus.valueOf(statusString);

        for (Requirement r : mgr.getRequirements())
        {
            r.setStatus(status.getValue());
            DMGame.get().getJpa().requirements.save(r);
        }
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "text/plain")
    public String getRequirementsStatus(@RequestParam Long processId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        Map<Integer, Integer> count = new HashMap<>();
        count.put(RequirementStatus.Unconfirmed.getValue(), 0);
        count.put(RequirementStatus.Editable.getValue(), 0);
        count.put(RequirementStatus.Confirmed.getValue(), 0);
        count.put(RequirementStatus.Enacted.getValue(), 0);
        count.put(RequirementStatus.Discarded.getValue(), 0);
        List<Requirement> requirements = proc.getRequirements();

        if (requirements.size() < 1)
        {
            return RequirementStatus.Unconfirmed.name();
        }

        for (Requirement r : requirements)
        {
            Integer n = count.get(r.getStatus());

            if (n == null)
            {
                continue;
            }

            count.put(r.getStatus(), n + 1);
        }

        if (count.get(RequirementStatus.Unconfirmed.getValue()) > 0)
        {
            return RequirementStatus.Unconfirmed.name();
        }

        if (count.get(RequirementStatus.Enacted.getValue()) > 0)
        {
            return RequirementStatus.Enacted.name();
        }

        if (count.get(RequirementStatus.Discarded.getValue()) > 0)
        {
            return RequirementStatus.Discarded.name();
        }

        return RequirementStatus.Confirmed.name();
    }

    @RequestMapping(value = "/statusmap", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Integer> getRequirementsStatusMap(@RequestParam Long processId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        Map<String, Integer> count = new HashMap<>();

        for (Requirement r : proc.getRequirements())
        {
            String str = RequirementStatus.valueOf(r.getStatus()).name();
            Integer n = count.get(str);

            if (n == null)
            {
                n = 0;
            }

            count.put(str, n + 1);
        }

        return count;
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.PUT)
    public void confirmRequirements(@RequestParam Long processId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);

        for (Requirement r : mgr.getRequirements())
        {
            RequirementStatus oldStatus = RequirementStatus.valueOf(r.getStatus());

            if (RequirementStatus.next(oldStatus).contains(RequirementStatus.Confirmed))
            {
                r.setStatus(RequirementStatus.Confirmed.getValue());
                DMGame.get().getJpa().requirements.save(r);
            }
        }
    }

    @RequestMapping(value = "/dependencies/submit", method = RequestMethod.POST)
    public void setDependencies(@RequestParam Long processId, @RequestBody Map<Long, List<Long>> dependencies)
    {
        for (Long requirementId : dependencies.keySet())
        {
            // Delete previously stored dependencies
            for (HRequirementDependency storedDependency : requirementsDependenciesJpa
                    .findByRequirementId(requirementId))
            {
                requirementsDependenciesJpa.delete(storedDependency);
            }

            // Store new dependencies
            for (Long dependencyId : dependencies.get(requirementId))
            {
                HRequirementDependency requirementDependency = new HRequirementDependency(requirementId, dependencyId);
                requirementsDependenciesJpa.save(requirementDependency);
            }
        }
    }

    @RequestMapping(value = "/dependencies/list", method = RequestMethod.GET)
    public Map<Long, List<Long>> getDependencies(@RequestParam Long processId)
    {
        List<Requirement> requirements = DMGame.get().getProcessManager(processId).getRequirements();

        Map<Long, List<Long>> dependencies = new HashMap<>();

        for (Requirement requirement : requirements)
        {
            Long requirementId = requirement.getRequirementId();
            List<Long> requirementDependencies = new ArrayList<>();
            List<HRequirementDependency> storedDependencies = requirementsDependenciesJpa
                    .findByRequirementId(requirementId);

            for (HRequirementDependency dependency : storedDependencies)
            {
                requirementDependencies.add(dependency.getDependencyId());
            }

            if (requirementDependencies.size() > 0)
            {
                dependencies.put(requirementId, requirementDependencies);
            }
        }

        return dependencies;
    }

    @RequestMapping(value = "/property/submit", method = RequestMethod.POST)
    public void setProperties(@RequestParam Long processId, @RequestParam Long requirementId,
            @RequestParam String propertyName, @RequestParam String propertyValue)
    {
        HRequirementProperty requirementProperty = new HRequirementProperty(requirementId, propertyName, propertyValue);
        requirementsPropertiesJpa.save(requirementProperty);
    }

    @RequestMapping(value = "/properties", method = RequestMethod.GET)
    public List<HRequirementProperty> getProperties(@RequestParam Long processId, @RequestParam Long requirementId)
    {
        return requirementsPropertiesJpa.findPropertiesByRequirementId(requirementId);
    }

    @RequestMapping(value = "/edit/collaboratively", method = RequestMethod.GET)
    public List<HActivity> getRequirementsEditingSession(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getOngoingActivities(AccessRequirementsEditingSession.NAME);
    }

    @RequestMapping(value = "/edit/collaboratively", method = RequestMethod.POST)
    public void createRequirementsEditingSession(@RequestParam(required = false) String act,
            @RequestParam Long processId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);

        if ("close".equals(act))
        {
            List<HActivity> activities = mgr.getOngoingActivities(AccessRequirementsEditingSession.NAME);

            for (HActivity a : activities)
            {
                mgr.deleteActivity(a);
            }
        }
        else
        {
            for (HProcessMember m : mgr.getProcessMembers())
            {
                mgr.createActivity(AccessRequirementsEditingSession.NAME, m.getUserId());
            }
        }
    }
}