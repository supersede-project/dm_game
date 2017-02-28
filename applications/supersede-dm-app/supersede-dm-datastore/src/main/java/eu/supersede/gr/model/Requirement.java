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

/**
* @author Andrea Sosi
**/

package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "requirements")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Requirement
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long requirementId;

    private String name;
    private String description;
    private Integer status;
    private Long processId;

    public Requirement()
    {
        processId = -1L;
        status = RequirementStatus.Unconfirmed.getValue();
    }

    public Requirement(String name, String description)
    {
        this();
        this.name = name;
        this.description = description;
    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public void setRequirementId(Long requirementId)
    {
        this.requirementId = requirementId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        if (status == null)
        {
            return RequirementStatus.Unconfirmed.getValue();
        }
        else
        {
            return status;
        }
    }

    public Long getProcessId()
    {
        if (processId == null)
        {
            return -1L;
        }
        else
        {
            return processId;
        }
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public String getTopic()
    {
        return "";
    }
}