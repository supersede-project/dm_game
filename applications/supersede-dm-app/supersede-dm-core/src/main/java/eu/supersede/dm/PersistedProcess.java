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

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.HPropertyBag;
import eu.supersede.gr.model.ProcessStatus;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;
import eu.supersede.gr.model.ValutationCriteria;

public class PersistedProcess extends AbstractProcessManager
{
    private Long processId;

    public PersistedProcess(Long processId)
    {
        this.processId = processId;
    }

    @Override
    public void addRequirement(Requirement r)
    {
        r.setProcessId(processId);
        DMGame.get().jpa.requirements.save(r);
    }

    @Override
    public List<Requirement> requirements()
    {
        return DMGame.get().jpa.requirements.findRequirementsByProcessId(processId);
    }

    @Override
    public int getRequirementsCount()
    {
        return requirements().size();
    }

    @Override
    public void setRequirementsStatus(List<Requirement> reqs, Integer status)
    {
        for (Requirement r : reqs)
        {
            if (isValidNextState(r.getStatus(), status))
            {
                r.setStatus(status);
                DMGame.get().jpa.requirements.save(r);
            }
        }
    }

    @Override
    public Long addProcessMember(Long userId, String role)
    {
        HProcessMember m = new HProcessMember();
        m.setProcessId(processId);
        m.setUserId(userId);
        m.setRole(role);
        m = DMGame.get().jpa.members.save(m);
        return m.getId();
    }

    @Override
    public List<HProcessMember> getProcessMembers()
    {
        return DMGame.get().jpa.members.findProcessMembers(processId);
    }

    @Override
    public List<HProcessMember> getProcessMembers(String role)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HActivity createActivity(String methodName, Long userId)
    {
        HActivity a = new HActivity();
        a.setProcessId(processId);
        a.setUserId(userId);
        a.setMethodName(methodName);
        return DMGame.get().jpa.activities.save(a);
    }

    @Override
    public void addAlert(HAlert alert)
    {
        alert.setProcessId(processId);
        DMGame.get().jpa.alerts.save(alert);
    }

    @Override
    public List<HAlert> getAlerts()
    {
        return DMGame.get().jpa.alerts.findAlertsByProcess(processId);
    }

    @Override
    public void removeAlert(String id)
    {
        HAlert alert = DMGame.get().jpa.alerts.findOne(id);
        alert.setProcessId(Long.MIN_VALUE);
        DMGame.get().jpa.alerts.save(alert);
    }

    @Override
    public List<HActivity> getOngoingActivities()
    {
        return new ArrayList<>();
    }

    @Override
    public void addCriterion(ValutationCriteria vc)
    {
        HProcessCriterion c = new HProcessCriterion();
        c.setSourceId(vc.getCriteriaId());
        c.setDescription(vc.getDescription());
        c.setProcessId(this.processId);
        c.setName(vc.getName());
        DMGame.get().jpa.processCriteria.save(c);
    }

    @Override
    public List<ValutationCriteria> getCriteria()
    {
        List<ValutationCriteria> list = new ArrayList<>();
        List<HProcessCriterion> procList = DMGame.get().jpa.processCriteria.findByProcessId(this.processId);

        for (HProcessCriterion pc : procList)
        {
            ValutationCriteria v = new ValutationCriteria();
            v.setCriteriaId(pc.getSourceId());
            v.setDescription(pc.getDescription());
            v.setName(pc.getName());
            v.setUserCriteriaPoints(new ArrayList<>());
            list.add(v);
        }

        return list;
    }

    @Override
    public List<HProcessCriterion> getProcessCriteria()
    {
        return DMGame.get().jpa.processCriteria.findByProcessId(this.processId);
    }

    @Override
    public int getCriteriaCount()
    {
        return DMGame.get().jpa.processCriteria.findByProcessId(this.processId).size();
    }

    @Override
    public List<HActivity> getOngoingActivities(String methodName)
    {
        return DMGame.get().jpa.activities.find(this.processId, methodName);
    }

    @Override
    public PropertyBag getProperties(HActivity a)
    {
        HPropertyBag bag = null;

        if (a.getPropertyBag() != null)
        {
            bag = DMGame.get().jpa.propertyBags.findOne(a.getPropertyBag());
        }

        if (bag == null)
        {
            bag = new HPropertyBag();
            bag = DMGame.get().jpa.propertyBags.save(bag);
            a.setPropertyBag(bag.getId());
            a = DMGame.get().jpa.activities.save(a);
        }

        return new PropertyBag(a);
    }

    public Requirement getRequirement(Long reqId)
    {
        return DMGame.get().getJpa().requirements.findOne(reqId);
    }

    @Override
    public void removeRequirement(Long reqId)
    {
        Requirement r = getRequirement(reqId);

        if (r != null)
        {
            r.setProcessId(-1L);
            r.setStatus(RequirementStatus.Unconfirmed.getValue());
            DMGame.get().jpa.requirements.save(r);
        }
    }

    @Override
    public void removeCriterion(Long criterionId, Long sourceId, Long processId)
    {
        DMGame.get().getJpa().processCriteria.deleteById(criterionId, sourceId, processId);
    }

    @Override
    public void removeProcessMember(Long id, Long userId, Long processId)
    {
        DMGame.get().getJpa().members.deleteById(id, userId, processId);
    }

    @Override
    public ProcessStatus getProcessStatus()
    {
        HProcess process = getProcess();
        return process.getStatus();
    }

    private HProcess getProcess()
    {
        return DMGame.get().getJpa().processes.findOne(this.processId);
    }

    @Override
    public void setProcessStatus(ProcessStatus status)
    {
        HProcess process = getProcess();
        process.setStatus(status);
        DMGame.get().getJpa().processes.save(process);
    }
}