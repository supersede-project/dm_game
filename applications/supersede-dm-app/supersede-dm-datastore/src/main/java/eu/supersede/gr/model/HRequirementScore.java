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
    private Long processId;

    @Id
    private String rankingName;

    @Id
    private Long requirementId;

    private Priority priority;

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public String getRankingName()
    {
        return rankingName;
    }

    public void setRankingName(String rankingName)
    {
        this.rankingName = rankingName;
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