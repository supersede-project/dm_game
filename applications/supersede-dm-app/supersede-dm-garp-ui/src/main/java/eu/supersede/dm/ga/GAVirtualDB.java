package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.dm.ga.data.GAGame;

public class GAVirtualDB {
	
	static GAVirtualDB instance = new GAVirtualDB();
	
	public static GAVirtualDB get() {
		return instance;
	}
	
	static class GameInfo {
		GAGame			game;
		List<String>	criteria		= new ArrayList<>();
		List<Long>		requirements	= new ArrayList<>();
		List<Long>		participants	= new ArrayList<>();
		Map<Long,List<String>> rankings	= new HashMap<>();
	}
	
	DuplicateMap<Long,GAGame>	ownedGames = new DuplicateMap<>();
	
	DuplicateMap<Long,GAGame>	activeGames = new DuplicateMap<>();
	
	Map<Long,GameInfo>			games = new HashMap<>();
	
	
	
	public GameInfo create( GAGame game ) {
		GameInfo gi = new GameInfo();
		gi.game = game;
		games.put( game.getId(), gi );
		ownedGames.put( game.getOwner(), game );
		for( Long provider : getParticipants( game ) ) {
			activeGames.put( provider, game );
		}
		return gi;
	}
	
	public void create( GAGame game, List<String> gameCriteria, List<Long> gameRequirements ) {
		GameInfo gi = create( game );
		gi.requirements = gameRequirements;
		gi.criteria = gameCriteria;
	}
	
	public List<GAGame> getOwnedGames( Long owner ) {
		return ownedGames.getList( owner );
	}
	
	public List<GAGame> getActiveGames( Long userId ) {
		return activeGames.getList( userId );
	}
	
	public GAGame getActiveGame(Long userId) {
		return activeGames.get( userId );
	}

	public void setRanking( Long gameId, Long userId, List<String> reqs ) {
		GameInfo gi = getGameInfo( gameId );
		if( gi == null ) {
			return;
		}
		gi.rankings.put( userId, reqs );
	}
	
	public GameInfo getGameInfo( Long gameId ) {
		return games.get( gameId );
	}
	
	public List<Long> getParticipants( GAGame game ) {
		GameInfo gi = getGameInfo( game.getId() );
		if( gi == null ) {
			return new ArrayList<>();
		}
		return gi.participants;
	}
	
	public List<String> getCriteria( GAGame game ) {
		GameInfo gi = getGameInfo( game.getId() );
		if( gi == null ) {
			return new ArrayList<>();
		}
		return gi.criteria;
	}
	
	public List<Long> getRequirements(GAGame game) {
		GameInfo gi = getGameInfo( game.getId() );
		if( gi == null ) {
			return new ArrayList<>();
		}
		return gi.requirements;
	}
	
}
