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

package eu.supersede.gr.model;

import java.io.Serializable;

public class RequirementPropertyId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long requirementId;
    private String propertyName;

    public RequirementPropertyId()
    {

    }

    public RequirementPropertyId(Long requirementId, String propertyName)
    {
        this.requirementId = requirementId;
        this.propertyName = propertyName;
    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public String getPropertyName()
    {
        return propertyName;
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

        RequirementPropertyId requirementPropertyId = (RequirementPropertyId) o;

        if (requirementId != requirementPropertyId.getRequirementId())
        {
            return false;
        }

        if (!propertyName.equals(requirementPropertyId.getPropertyName()))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (requirementId ^ (requirementId >>> 32));
        result = 31 * result + propertyName.hashCode();
        return result;
    }
}