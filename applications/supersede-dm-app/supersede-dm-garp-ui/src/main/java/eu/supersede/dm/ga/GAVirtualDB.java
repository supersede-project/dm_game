package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.dm.ga.data.GameInfo;

public class GAVirtualDB
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static GAVirtualDB instance = new GAVirtualDB();

    public static GAVirtualDB get()
    {
        return instance;
    }

    DuplicateMap<Long, GAGame> ownedGames = new DuplicateMap<>();
    DuplicateMap<Long, GAGame> activeGames = new DuplicateMap<>();
    Map<Long, GameInfo> games = new HashMap<>();

    public GameInfo create(GAGame game)
    {
        GameInfo gi = new GameInfo();
        gi.setGame(game);
        games.put(game.getId(), gi);
        ownedGames.put(game.getOwner(), game);
        return gi;
    }

    public void create(GAGame game, List<String> criteria, List<Long> requirements, List<Long> participants)
    {
        GameInfo gi = create(game);
        gi.setRequirements(requirements);
        gi.setCriteria(criteria);
        gi.setParticipants(participants);

        for (Long provider : participants)
        {
            activeGames.put(provider, game);
        }

        log.info("Created game: " + game.getId() + ", requirements: " + gi.getRequirements().size() + ", criteria: "
                + gi.getCriteria().size() + ", participants: " + gi.getParticipants().size());
    }

    public List<GAGame> getOwnedGames(Long owner)
    {
        return ownedGames.getList(owner);
    }

    public List<GAGame> getActiveGames(Long userId)
    {
        return activeGames.getList(userId);
    }

    public GAGame getActiveGame(Long userId)
    {
        return activeGames.get(userId);
    }

    // public void setRanking(Long gameId, Long userId, List<Long> reqs)
    // {
    // GameInfo gi = getGameInfo(gameId);
    //
    // if (gi == null)
    // {
    // return;
    // }
    //
    // gi.rankings.put(userId, reqs);
    // }

    public void setRanking(Long gameId, Long userId, Map<String, List<Long>> reqs)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return;
        }

        Map<String, List<Long>> map = gi.getRankings().get(userId);

        if (map == null)
        {
            map = new HashMap<>();
            gi.getRankings().put(userId, map);
        }

        for (String key : reqs.keySet())
        {
            map.put(key, reqs.get(key));
        }

        // gi.rankings.put(userId, reqs);
    }

    public List<Long> getRankingsCriterion(Long gameId, Long userId, String criterion)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return null;
        }
        Map<String, List<Long>> map = gi.getRankings().get(userId);
        if (map == null)
        {
            return null;
        }

        return map.get(criterion);
    }

    public Map<String, List<Long>> getRanking(Long gameId, Long userId)
    {
        GameInfo gi = getGameInfo(gameId);
        return gi.getRankings().get(userId);
    }

    public GameInfo getGameInfo(Long gameId)
    {
        return games.get(gameId);
    }

    public List<Long> getParticipants(GAGame game)
    {
        GameInfo gi = getGameInfo(game.getId());

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();
    }

    public List<Long> getParticipants(Long gameId)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();

    }

    public List<String> getCriteria(GAGame game)
    {
        return getCriteria(game.getId());
    }

    public List<String> getCriteria(long gameId)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getCriteria();
    }

    public List<Long> getRequirements(Long gameId)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getRequirements();
    }

    public Map<String, List<Long>> getRequirements(Long gameId, Long userId)
    {
        GameInfo gi = getGameInfo(gameId);
        if (gi == null)
        {
            return new HashMap<>();
        }
        Map<String, List<Long>> map = new HashMap<>();
        for (String c : gi.getCriteria())
        {
            map.put(c, gi.getRequirements());
        }
        return map;
    }
}