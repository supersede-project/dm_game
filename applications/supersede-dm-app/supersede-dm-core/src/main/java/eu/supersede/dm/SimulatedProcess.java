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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.ProcessStatus;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

public class SimulatedProcess extends AbstractProcessManager
{
    private static Long counter = 0L;

    private Long processId;
    private List<Requirement> requirements;
    private Map<Long, List<HProcessMember>> members;
    private Map<Long, List<HActivity>> activities;
    
    String phaseName = DMGame.get().getLifecycle().getInitPhase().getName();
    
    public SimulatedProcess(Long processId)
    {
        requirements = new ArrayList<>();
        members = new HashMap<>();
        activities = new HashMap<>();
        this.processId = processId;
    }

    @Override
    public void addRequirement(Requirement req)
    {
        requirements.add(req);
    }

    @Override
    public int getRequirementsCount()
    {
        return requirements.size();
    }

    @Override
    public List<Requirement> requirements()
    {
        return requirements;
    }

    @Override
    public void setRequirementsStatus(List<Requirement> reqs, Integer status)
    {
        for (Requirement r : reqs)
        {
            if (isValidNextState(r.getStatus(), status))
            {
                r.setStatus(status);
            }
        }
    }

    @Override
    public Long addProcessMember(Long userId, String role)
    {
        List<HProcessMember> list = members.get(processId);

        if (list == null)
        {
            list = new ArrayList<>();
            members.put(processId, list);
        }

        HProcessMember m = new HProcessMember();
        m.setId((++counter));
        m.setProcessId(processId);
        m.setUserId(userId);
        m.setRole(role);
        list.add(m);
        return m.getId();
    }

    @Override
    public List<HProcessMember> getProcessMembers()
    {
        return members.get(processId);
    }

    @Override
    public List<HProcessMember> getProcessMembers(String role)
    {
        List<HProcessMember> list = members.get(processId);

        if (list == null)
        {
            return null;
        }

        List<HProcessMember> ret = new ArrayList<>();

        for (HProcessMember m : list)
        {
            if (role.equals(m.getRole()))
            {
                ret.add(m);
            }
        }

        return ret;
    }

    @Override
    public HActivity createActivity(String methodName, Long userId)
    {
        List<HActivity> list = activities.get(processId);

        if (list == null)
        {
            list = new ArrayList<>();
            activities.put(processId, list);
        }

        HActivity a = new HActivity();
        a.setId((++counter));
        a.setProcessId(processId);
        a.setUserId(userId);
        a.setMethodName(methodName);
        list.add(a);
        return a;
    }

    @Override
    public void addAlert(HAlert alert)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public List<HAlert> getAlerts()
    {
        return new ArrayList<>();
    }

    @Override
    public void removeAlert(String id)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public List<HActivity> getOngoingActivities()
    {
        List<HActivity> list = activities.get(processId);

        if (list == null)
        {
            list = new ArrayList<>();
        }

        return list;
    }

    @Override
    public void addCriterion(ValutationCriteria criterion)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public List<ValutationCriteria> getCriteria()
    {
        return new ArrayList<>();
    }

    @Override
    public int getCriteriaCount()
    {
        return 0;
    }

    @Override
    public List<HActivity> getOngoingActivities(String methodName)
    {
        List<HActivity> all = getOngoingActivities();
        List<HActivity> sel = new ArrayList<>();

        for (HActivity a : all)
        {
            if (a.getMethodName().equals(methodName))
            {
                sel.add(a);
            }
        }

        return sel;
    }

    @Override
    public PropertyBag getProperties(HActivity a)
    {
        return new PropertyBag();
    }

    @Override
    public List<HProcessCriterion> getProcessCriteria()
    {
        return new ArrayList<>();
    }

    @Override
    public void removeRequirement(Long reqId)
    {
        int pos = -1;
        for (int i = 0; i < requirements.size(); i++)
        {
            if (requirements.get(i).getRequirementId() == reqId)
            {
                pos = i;
                break;
            }
        }
        if (pos != -1)
        {
            requirements.remove(pos);
        }
    }

    @Override
    public void removeCriterion(Long criterionId, Long sourceId, Long processId)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeProcessMember(Long id, Long userId, Long processId)
    {
        if (members.get(id) != null)
        {
            members.remove(id);
        }
    }

    @Override
    public ProcessStatus getProcessStatus()
    {
        return ProcessStatus.InProgress;
    }

    @Override
    public void setProcessStatus(ProcessStatus status)
    {
        // TODO Auto-generated method stub

    }

	@Override
	public Requirement getRequirement(Long reqId) {
		for( Requirement r : requirements ) {
			if( r.getRequirementId() == reqId ) {
				return r;
			}
		}
		return null;
	}

	@Override
	public void deleteActivity(HActivity a) {
		List<HActivity> actList = activities.get( this.processId );
		if( actList == null ) {
			return;
		}
		for( HActivity existing : actList ) {
			if( existing.getId() == a.getId() ) {
				actList.remove( a.getId() );
				return;
			}
		}
	}

	@Override
	public String getCurrentPhase() {
		return this.phaseName;
	}

	@Override
	public Collection<String> getNextPhases() {
		DMLifecycle lifecycle = DMGame.get().getLifecycle();
		DMPhase phase = lifecycle.getPhase( this.phaseName );
		List<String> phases = new ArrayList<>();
		for( DMPhase n : phase.getNextPhases() ) {
			phases.add( n.getName() );
		}
		return phases;
	}

	@Override
	public void setNextPhase(String phaseName) throws Exception {
		try {
			DMGame.get().getLifecycle().getPhase( this.phaseName ).checkPreconditions( this );
			this.phaseName = phaseName;
		}
		catch( Exception ex ) {
			throw ex;
		}
	}
}