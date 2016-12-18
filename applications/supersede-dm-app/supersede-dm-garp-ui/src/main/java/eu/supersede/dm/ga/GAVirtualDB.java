package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.supersede.dm.ga.data.GAGame;

public class GAVirtualDB
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static GAVirtualDB instance = new GAVirtualDB();

    public static GAVirtualDB get()
    {
        return instance;
    }

    static class GameInfo
    {
        GAGame game;
        List<String> criteria = new ArrayList<>();
        List<Long> requirements = new ArrayList<>();
        List<Long> participants = new ArrayList<>();
        Map<Long, Map<String,List<Long>>> rankings = new HashMap<>();
    }

    DuplicateMap<Long, GAGame> ownedGames = new DuplicateMap<>();
    DuplicateMap<Long, GAGame> activeGames = new DuplicateMap<>();
    Map<Long, GameInfo> games = new HashMap<>();

    public GameInfo create(GAGame game)
    {
        GameInfo gi = new GameInfo();
        gi.game = game;
        games.put(game.getId(), gi);
        ownedGames.put(game.getOwner(), game);
        return gi;
    }

    public void create(GAGame game, List<String> criteria, List<Long> requirements, List<Long> participants)
    {
        GameInfo gi = create(game);
        gi.requirements = requirements;
        gi.criteria = criteria;
        gi.participants = participants;

        for (Long provider : participants)
        {
            activeGames.put(provider, game);
        }

        log.info("Created game: " + game.getId() + ", requirements: " + gi.requirements.size() + ", criteria: "
                + gi.criteria.size() + ", participants: " + gi.participants.size());
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

//    public void setRanking(Long gameId, Long userId, List<Long> reqs)
//    {
//        GameInfo gi = getGameInfo(gameId);
//
//        if (gi == null)
//        {
//            return;
//        }
//
//        gi.rankings.put(userId, reqs);
//    }

    public void setRanking(Long gameId, Long userId, Map<String,List<Long>> reqs)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return;
        }
        
        gi.rankings.put(userId, reqs);
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

        return gi.participants;
    }

    public List<String> getCriteria(GAGame game)
    {
        GameInfo gi = getGameInfo(game.getId());

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.criteria;
    }

    public List<Long> getRequirements(Long gameId)
    {
        GameInfo gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.requirements;
    }

	public Map<String,List<Long>> getRequirements( Long gameId, Long userId ) {
		GameInfo gi = getGameInfo( gameId );
		if( gi == null ) {
			return new HashMap<>();
		}
		Map<String,List<Long>> map = new HashMap<>();
		for( String c : gi.criteria ) {
			map.put( c, gi.requirements );
		}
		return map;
	}
}