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

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

public interface ProcessManager
{
    // Requirements

    public void addRequirement(Requirement r);

    public List<Requirement> requirements();

    public int getRequirementsCount();

    public void setRequirementsStatus(List<Requirement> reqs, Integer status);

    public void removeRequirement(Long reqId);

    // Criteria

    public void addCriterion(ValutationCriteria criterion);

    public List<ValutationCriteria> getCriteria();

    public List<HProcessCriterion> getProcessCriteria();

    public int getCriteriaCount();

    public void removeCriterion(Long criterionId, Long sourceId, Long processId);

    // Alerts

    public void addAlert(HAlert alert);

    public List<HAlert> getAlerts();

    // Members

    public Long addProcessMember(Long userId, String role);

    public List<HProcessMember> getProcessMembers();

    public List<HProcessMember> getProcessMembers(String role);

    public void removeProcessMember(Long mId);

    // Activities

    public HActivity createActivity(String methodName, Long userId);

    public List<HActivity> getOngoingActivities();

    public List<HActivity> getOngoingActivities(String methodName);

    // Properties

    public PropertyBag getProperties(HActivity a);
}