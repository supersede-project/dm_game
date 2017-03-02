package eu.supersede.gr.model.id;

import java.io.Serializable;

public class PlayerWeightId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long gameId;
    private Long criterionId;
    private Long userId;

    public PlayerWeightId()
    {

    }

    public PlayerWeightId(Long gameId, Long criterionId, Long userId)
    {
        this.gameId = gameId;
        this.criterionId = criterionId;
        this.userId = userId;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public Long getCriterionId()
    {
        return criterionId;
    }

    public Long getUserId()
    {
        return userId;
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

        PlayerWeightId playerWeightId = (PlayerWeightId) o;

        if (gameId != playerWeightId.getGameId())
        {
            return false;
        }

        if (criterionId != playerWeightId.getCriterionId())
        {
            return false;
        }

        if (userId != playerWeightId.getUserId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (gameId ^ (gameId >>> 32));
        result = 31 * result + (int) (criterionId ^ (criterionId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}