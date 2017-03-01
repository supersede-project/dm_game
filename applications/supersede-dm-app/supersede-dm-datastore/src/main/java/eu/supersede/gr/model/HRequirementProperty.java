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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(RequirementPropertyId.class)
@Table(name = "h_requirements_properties")
public class HRequirementProperty
{
    @Id
    private Long requirementId;

    @Id
    private String propertyName;

    private String propertyValue;
    // private Double priority;

    public HRequirementProperty()
    {

    }

    public HRequirementProperty(Long requirementId, String propertyName, String propertyValue)
    {
        this.requirementId = requirementId;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public void setRequirementId(Long requirementId)
    {
        this.requirementId = requirementId;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getPropertyValue()
    {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue)
    {
        this.propertyValue = propertyValue;
    }
}