package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.gr.model.Requirement;

public class GAVirtualDB {
	
	static GAVirtualDB instance = new GAVirtualDB();
	
	public static GAVirtualDB get() {
		return instance;
	}
	
	static class GameInfo {
		GAGame			game;
		List<String>	criteria		= new ArrayList<>();
		List<String>	requirements	= new ArrayList<>();
		List<Long>		participants	= new ArrayList<>();
	}
	
	DuplicateMap<Long,GAGame>	ownedGames = new DuplicateMap<>();
	
	DuplicateMap<Long,GAGame>	activeGames = new DuplicateMap<>();
	
	Map<Long,GameInfo>			games = new HashMap<>();
	
	
	
	public void create( GAGame game ) {
		ownedGames.put( game.getOwner(), game );
		for( Long provider : getParticipants( game ) ) {
			activeGames.put( provider, game );
		}
	}
	
	public List<GAGame> getOwnedGames( Long owner ) {
		return ownedGames.getList( owner );
	}

	public GAGame getActiveGame(Long userId) {
		return activeGames.get( userId );
	}

	public void setRanking( String gameId, Long userId, List<Requirement> reqs ) {
		// TODO Auto-generated method stub
		
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
	
	public List<String> getRequirements(GAGame game) {
		GameInfo gi = getGameInfo( game.getId() );
		if( gi == null ) {
			return new ArrayList<>();
		}
		return gi.requirements;
	}
	
}
