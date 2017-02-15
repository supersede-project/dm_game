package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_ga_solutions")
public class HGASolution
{
    @Id
    private Long gameId;

    private String jsonizedSolution;

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
}