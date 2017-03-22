package eu.supersede.gr.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}