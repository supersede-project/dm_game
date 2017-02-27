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
    private Long dependerId;

    @Id
    private Long dependeeId;

    public Long getDependerId()
    {
        return dependerId;
    }

    public void setDependerId(Long dependerId)
    {
        this.dependerId = dependerId;
    }

    public Long getDependeeId()
    {
        return dependeeId;
    }

    public void setDependeeId(Long dependeeId)
    {
        this.dependeeId = dependeeId;
    }
}