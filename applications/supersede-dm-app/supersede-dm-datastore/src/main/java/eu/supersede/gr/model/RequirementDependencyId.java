package eu.supersede.gr.model;

import java.io.Serializable;

public class RequirementDependencyId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long dependerId;
    private Long dependeeId;

    public RequirementDependencyId()
    {

    }

    public RequirementDependencyId(Long dependerId, Long dependeeId)
    {
        this.dependerId = dependerId;
        this.dependeeId = dependeeId;
    }

    public Long getDependerId()
    {
        return dependerId;
    }

    public Long getDependeeId()
    {
        return dependeeId;
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

        RequirementDependencyId requirementDependencyId = (RequirementDependencyId) o;

        if (dependerId != requirementDependencyId.getDependerId())
        {
            return false;
        }

        if (dependeeId != requirementDependencyId.getDependeeId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (dependerId ^ (dependerId >>> 32));
        result = 31 * result + (int) (dependeeId ^ (dependeeId >>> 32));
        return result;
    }
}