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
import javax.persistence.IdClass;
import javax.persistence.Table;

import eu.supersede.gr.model.id.ProcessCriterionId;

@Entity
@IdClass(ProcessCriterionId.class)
@Table(name = "h_process_criteria")
public class HProcessCriterion
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long criterionId;

    @Id
    private Long sourceId;

    @Id
    private Long processId;

    private String name;
    private String description;

    public HProcessCriterion()
    {
    }

    public HProcessCriterion(String name)
    {
        this.name = name;
    }

    public Long getCriterionId()
    {
        return criterionId;
    }

    public void setCriterionId(Long criterionId)
    {
        this.criterionId = criterionId;
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

    public Long getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(Long sourceId)
    {
        this.sourceId = sourceId;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }
}