package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(RequirementDependencyId.class)
@Table(name = "h_requirements_dependencies")
public class HRequirementDependency
{
    @Id
    private Long requirementId;

    @Id
    private Long dependencyId;

    public HRequirementDependency()
    {

    }

    public HRequirementDependency(Long requirementId, Long dependencyId)
    {
        this.requirementId = requirementId;
        this.dependencyId = dependencyId;

    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public void setRequirementId(Long requirementId)
    {
        this.requirementId = requirementId;
    }

    public Long getDependencyId()
    {
        return dependencyId;
    }

    public void setDependencyId(Long dependencyId)
    {
        this.dependencyId = dependencyId;
    }
}