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

package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;

public class CreateRequirementsEditingSession implements DMMethod
{
    public static final String NAME = "Open Collaborative Requirements Editing Session";

    private String name;
    private List<DMRoleSpec> list;
    private List<DMOption> options;

    public CreateRequirementsEditingSession()
    {
        this.name = NAME;
        list = new ArrayList<>();
        options = new ArrayList<>();
    }

    @Override
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public DMObjective getObjective()
    {
        return DMObjective.PrioritizeRequirements;
    }

    @Override
    public List<DMRoleSpec> getRoleList()
    {
        return list;
    }

    @Override
    public List<DMOption> getOptions()
    {
        return this.options;
    }

    @Override
    public List<DMCondition> preconditions()
    {
        List<DMCondition> list = new ArrayList<DMCondition>();

        list.add(new DMCondition()
        {
            @Override
            public boolean isTrue(ProcessManager mgr)
            {
            	return "Editing".equals( mgr.getCurrentPhase() );
            }
        });

        return list;
    }

    @Override
    public String getPage(ProcessManager mgr)
    {
        return "create_req_edit_session";
    }

	@Override
	public String getDescription(ProcessManager arg0) {
		return "Manage Collaborative Requirements Editing Session";
	}

	@Override
	public String getLabel(ProcessManager arg0) {
		return "Manage Collaborative Requirements Editing Session";
	}
}