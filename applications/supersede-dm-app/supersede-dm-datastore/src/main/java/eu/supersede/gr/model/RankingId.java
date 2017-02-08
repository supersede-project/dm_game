package eu.supersede.gr.model;

import java.io.Serializable;

public class RankingId implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Long gameId;
    private Long userId;

    public RankingId()
    {

    }

    public RankingId(Long gameId, Long userId)
    {
        this.gameId = gameId;
        this.userId = userId;
    }

    public Long getGameId()
    {
        return gameId;
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

        RankingId rankingId = (RankingId) o;

        if (gameId != rankingId.getGameId())
        {
            return false;
        }

        if (userId != rankingId.getUserId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (gameId ^ (gameId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}