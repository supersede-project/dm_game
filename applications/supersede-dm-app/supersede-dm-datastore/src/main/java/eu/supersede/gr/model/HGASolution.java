package eu.supersede.gr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "h_ga_solutions")
public class HGASolution
{
    @Id
    private Long gameId;

    @Column(columnDefinition = "varchar(5000)")
    private String jsonizedSolution;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public String getJsonizedSolution()
    {
        return jsonizedSolution;
    }

    public void setJsonizedSolution(String jsonizedSolution)
    {
        this.jsonizedSolution = jsonizedSolution;
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