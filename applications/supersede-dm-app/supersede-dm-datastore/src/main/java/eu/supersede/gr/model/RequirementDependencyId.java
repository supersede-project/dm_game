package eu.supersede.gr.model;

import java.io.Serializable;

public class RequirementDependencyId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long requirementId;
    private Long dependencyId;

    public RequirementDependencyId()
    {

    }

    public RequirementDependencyId(Long requirementId, Long dependencyId)
    {
        this.requirementId = requirementId;
        this.dependencyId = dependencyId;
    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public Long getDependencyId()
    {
        return dependencyId;
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

        if (requirementId != requirementDependencyId.getRequirementId())
        {
            return false;
        }

        if (dependencyId != requirementDependencyId.getDependencyId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (requirementId ^ (requirementId >>> 32));
        result = 31 * result + (int) (dependencyId ^ (dependencyId >>> 32));
        return result;
    }
}