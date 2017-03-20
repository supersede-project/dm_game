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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DMPhase
{
    private String name;
    private Map<String, DMPhase> nexts;
    private Map<String, DMPhase> prevs;

    public DMPhase(String name)
    {
        this.name = name;
        nexts = new HashMap<>();
        prevs = new HashMap<>();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addNext(DMPhase phase)
    {
        nexts.put(phase.getName(), phase);
        phase.prevs.put(this.name, this);
    }

    public Collection<DMPhase> getNextPhases()
    {
        return nexts.values();
    }

    public Collection<DMPhase> getPrevPhases()
    {
        return prevs.values();
    }

    public boolean isAllowed(ProcessManager mgr)
    {
        try
        {
            checkPreconditions(mgr);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public void checkPreconditions(ProcessManager mgr) throws Exception
    {
        // By default, no constraints on the transition exist
    }

    public void activate(ProcessManager mgr)
    {
        // To be overridden by child classes
    }
}