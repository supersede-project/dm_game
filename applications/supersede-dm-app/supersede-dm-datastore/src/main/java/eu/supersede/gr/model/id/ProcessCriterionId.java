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

public class ProcessCriterionId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long criterionId;
    private Long sourceId;
    private Long processId;

    public ProcessCriterionId()
    {

    }

    public ProcessCriterionId(Long criterionId, Long sourceId, Long processId)
    {
        this.criterionId = criterionId;
        this.sourceId = sourceId;
        this.processId = processId;
    }

    public Long getCriterionId()
    {
        return criterionId;
    }

    public Long getSourceId()
    {
        return sourceId;
    }

    public Long getProcessId()
    {
        return processId;
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

        ProcessCriterionId processCriterionId = (ProcessCriterionId) o;

        if (criterionId != processCriterionId.getCriterionId())
        {
            return false;
        }

        if (sourceId != processCriterionId.getSourceId())
        {
            return false;
        }

        if (processId != processCriterionId.getProcessId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (criterionId ^ (criterionId >>> 32));
        result = 31 * result + (int) (sourceId ^ (sourceId >>> 32));
        result = 31 * result + (int) (processId ^ (processId >>> 32));
        return result;
    }
}