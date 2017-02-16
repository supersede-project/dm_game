package eu.supersede.gr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(RankingId.class)
@Table(name = "h_ga_rankings")
public class HGARankingInfo
{
    @Id
    private Long gameId;

    @Id
    private Long userId;

    @Column(columnDefinition = "varchar(5000)")
    private String jsonizedRanking;

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getJsonizedRanking()
    {
        return jsonizedRanking;
    }

    public void setJsonizedRanking(String jsonizedRanking)
    {
        this.jsonizedRanking = jsonizedRanking;
    }
}