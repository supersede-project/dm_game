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