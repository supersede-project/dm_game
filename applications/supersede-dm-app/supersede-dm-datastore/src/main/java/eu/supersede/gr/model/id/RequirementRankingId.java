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

package eu.supersede.gr.model.id;

import java.io.Serializable;

public class RequirementRankingId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long processId;
    private String name;

    public RequirementRankingId()
    {

    }

    public RequirementRankingId(Long processId, String name)
    {
        this.processId = processId;
        this.name = name;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        RequirementRankingId requirementRankingId = (RequirementRankingId) o;

        if (processId != requirementRankingId.getProcessId())
        {
            return false;
        }

        if (!name.equals(requirementRankingId.getName()))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (processId ^ (processId >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }
}