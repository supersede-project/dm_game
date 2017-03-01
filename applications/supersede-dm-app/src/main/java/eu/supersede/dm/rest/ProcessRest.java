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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ActivityDetails;
import eu.supersede.dm.ActivityEntry;
import eu.supersede.dm.DMGame;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.ProcessRole;
import eu.supersede.dm.PropertyBag;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.RequirementsDependenciesJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.HProperty;
import eu.supersede.gr.model.HRequirementDependency;
import eu.supersede.gr.model.HRequirementProperty;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("processes")
public class ProcessRest
{
    @Autowired
    private RequirementsDependenciesJpa requirementsDependenciesJpa;

    @Autowired
    private RequirementsPropertiesJpa requirementsPropertiesJpa;

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Long newProcess()
    {
        return DMGame.get().createEmptyProcess().getId();
    }

    static class JqxProcess
    {
        public String id;
        public String name;
        public String state;
        public String date;
        public String objective;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<JqxProcess> getProcessList()
    {
        List<JqxProcess> list = new ArrayList<JqxProcess>();
        for (HProcess p : DMGame.get().getJpa().processes.findAll())
        {
            JqxProcess qp = new JqxProcess();
            qp.id = "" + p.getId();
            qp.name = p.getName();
            qp.state = p.getStatus().name();
            qp.objective = p.getObjective();
            qp.date = p.getStartTime().toString();
            list.add(qp);
        }
        return list;
    }

    public void addRequirements(Long procId, List<Long> reqList)
    {
        for (Long reqId : reqList)
        {
            Requirement r = DMGame.get().getJpa().requirements.findOne(reqId);
            if (r == null)
                continue;
            if (r.getProcessId() != -1)
                continue;
            r.setProcessId(procId);
            DMGame.get().getJpa().requirements.save(r);
        }
    }

    public void setRequirementsStatus(Long procId, List<Long> reqList, RequirementStatus status)
    {
        for (Long reqId : reqList)
        {
            Requirement r = DMGame.get().getJpa().requirements.findOne(reqId);
            if (r == null)
                continue;
            RequirementStatus oldStatus = RequirementStatus.valueOf(r.getStatus());
            if (RequirementStatus.next(oldStatus).contains(status))
            {
                r.setStatus(status.getValue());
                DMGame.get().getJpa().requirements.save(r);
            }
        }
    }

    @RequestMapping(value = "/available_activities", method = RequestMethod.GET)
    public List<ActivityEntry> getNextActivities(Long procId)
    {
        return DMGame.get().getProcessManager(procId).findNextActivities(DMLibrary.get().methods());
    }

    @RequestMapping(value = "/users/import", method = RequestMethod.POST)
    public void importUsers(@RequestParam Long procId, @RequestParam List<Long> idlist)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        if (idlist == null)
        {
            return;
        }
        for (Long userid : idlist)
        {
            proc.addProcessMember(userid, ProcessRole.User.name());
        }
    }

    @RequestMapping(value = "/criteria/import", method = RequestMethod.POST)
    public void importCriteria(@RequestParam Long procId, @RequestParam List<Long> idlist)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        if (idlist == null)
        {
            return;
        }
        for (Long cid : idlist)
        {
            ValutationCriteria c = DMGame.get().getCriterion(cid);
            proc.addCriterion(c);
        }
    }

    @RequestMapping(value = "/requirements/import", method = RequestMethod.POST)
    public void importRequirements(@RequestParam Long procId, @RequestParam List<Long> requirementsId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);

        if (requirementsId == null)
        {
            return;
        }

        for (Long requirementId : requirementsId)
        {
            Requirement r = DMGame.get().getJpa().requirements.findOne(requirementId);

            if (r == null)
            {
                continue;
            }

            r.setStatus(RequirementStatus.Editable.getValue());
            proc.addRequirement(r);
        }
    }

    @RequestMapping(value = "/users/list", method = RequestMethod.GET)
    public List<Long> getUserList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<Long> list = new ArrayList<>();
        for (HProcessMember member : proc.getProcessMembers())
        {
            list.add(member.getId());
        }
        return list;
    }

    @RequestMapping(value = "/users/list/detailed", method = RequestMethod.GET)
    public List<User> getUserObjectList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<User> list = new ArrayList<>();
        for (HProcessMember member : proc.getProcessMembers())
        {
            User u = DMGame.get().getJpa().users.findOne(member.getUserId());
            if (u != null)
            {
                list.add(u);
            }
        }
        return list;
    }

    @RequestMapping(value = "/criteria/list", method = RequestMethod.GET)
    public List<Long> getCriteriaList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<Long> list = new ArrayList<>();
        for (ValutationCriteria c : proc.getCriteria())
        {
            list.add(c.getCriteriaId());
        }
        return list;
    }

    @RequestMapping(value = "/criteria/list/detailed", method = RequestMethod.GET)
    public List<HProcessCriterion> getCriteriaObjectList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        return proc.getProcessCriteria();
    }

    @RequestMapping(value = "/requirements/list", method = RequestMethod.GET)
    public List<Requirement> getRequirementsList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        return proc.requirements();
    }

    @RequestMapping(value = "/requirements/count", method = RequestMethod.GET)
    public int getRequirementsCount(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        return proc.getRequirementsCount();
    }

    @RequestMapping(value = "/requirements/status", method = RequestMethod.GET, produces = "text/plain")
    public String getRequirementsStatus(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        Map<Integer, Integer> count = new HashMap<>();
        count.put(RequirementStatus.Unconfirmed.getValue(), 0);
        count.put(RequirementStatus.Confirmed.getValue(), 0);
        count.put(RequirementStatus.Enacted.getValue(), 0);
        count.put(RequirementStatus.Discarded.getValue(), 0);
        List<Requirement> requirements = proc.requirements();
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

    @RequestMapping(value = "/activities/list", method = RequestMethod.GET)
    public List<ActivityDetails> getActivityList(Authentication auth)
    {
        List<ActivityDetails> list = new ArrayList<>();
        List<HActivity> activities = DMGame.get()
                .getPendingActivities(((DatabaseUser) auth.getPrincipal()).getUserId());
        for (HActivity a : activities)
        {
            ActivityDetails d = new ActivityDetails();
            d.setActivityId(a.getId());
            d.setMethodName(a.getMethodName());
            d.setProcessId(a.getProcessId());
            d.setUserId(a.getUserId());
            ProcessManager mgr = DMGame.get().getProcessManager(a.getProcessId());
            DMMethod m = DMLibrary.get().getMethod(a.getMethodName());
            if (m != null)
            {
                d.setUrl(m.getPage(mgr));
                list.add(d);
            }
            PropertyBag bag = mgr.getProperties(a);
            for (HProperty p : bag.properties())
            {
                d.setProperty(p.getKey(), p.getValue());
            }
        }
        return list;
    }

    @RequestMapping(value = "/activities/groups", method = RequestMethod.GET)
    public Map<String, List<Long>> getActivityGroups(@RequestParam Long procId)
    {

        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        List<HActivity> activities = mgr.getOngoingActivities();

        Map<String, List<Long>> map = new HashMap<>();

        for (HActivity a : activities)
        {

            List<Long> list = map.get(a.getMethodName());
            if (list == null)
            {
                list = new ArrayList<>();
                map.put(a.getMethodName(), list);
            }
            list.add(a.getUserId());

        }

        return map;
    }

    @RequestMapping(value = "/requirements/confirm", method = RequestMethod.PUT)
    public void confirmRequirements(@RequestParam Long procId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        for (Requirement r : mgr.requirements())
        {
            if (r == null)
                continue;
            RequirementStatus oldStatus = RequirementStatus.valueOf(r.getStatus());
            if (RequirementStatus.next(oldStatus).contains(RequirementStatus.Confirmed))
            {
                r.setStatus(RequirementStatus.Confirmed.getValue());
                DMGame.get().getJpa().requirements.save(r);
            }
        }
    }

    @RequestMapping(value = "/requirements/dependencies/submit", method = RequestMethod.POST)
    public void setDependencies(@RequestParam Long procId, @RequestBody Map<Long, List<Long>> dependencies)
    {
        for (Long requirementId : dependencies.keySet())
        {
            for (Long dependencyId : dependencies.get(requirementId))
            {
                HRequirementDependency requirementDependency = new HRequirementDependency(requirementId, dependencyId);

                requirementsDependenciesJpa.save(requirementDependency);
            }
        }
    }

    @RequestMapping(value = "/requirements/close", method = RequestMethod.POST)
    public void closeProcess(@RequestParam Long procId)
    {
        DMGame.get().deleteProcess(procId);
    }

    @RequestMapping(value = "/requirements/property/submit", method = RequestMethod.POST)
    public void setProperties(@RequestParam Long procId, @RequestParam Long requirementId,
            @RequestParam String propertyName, @RequestParam String propertyValue)
    {
        HRequirementProperty requirementProperty = new HRequirementProperty(requirementId, propertyName, propertyValue);
        requirementsPropertiesJpa.save(requirementProperty);
    }

    @RequestMapping(value = "/requirements/properties", method = RequestMethod.GET)
    public List<HRequirementProperty> getProperties(@RequestParam Long procId, @RequestParam Long requirementId)
    {
        return requirementsPropertiesJpa.findPropertiesByRequirementId(requirementId);
    }
}