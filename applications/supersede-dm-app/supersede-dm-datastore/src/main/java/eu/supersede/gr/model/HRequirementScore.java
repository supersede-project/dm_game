package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import eu.supersede.gr.model.id.RequirementScoreId;

@Entity
@IdClass(RequirementScoreId.class)
@Table(name = "h_requirements_scores")
public class HRequirementScore
{
    @Id
    private Long rankingId;

    @Id
    private Long requirementId;

    private Priority priority;

    public Long getRankingId()
    {
        return rankingId;
    }

    public void setRankingId(Long rankingId)
    {
        this.rankingId = rankingId;
    }

    public Long getRequirementId()
    {
        return requirementId;
    }

    public void setRequirementId(Long requirementId)
    {
        this.requirementId = requirementId;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }
}