package eu.supersede.dm.ga;

import java.util.List;

import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.gr.model.Requirement;

public class GAVirtualDB {
	
	static GAVirtualDB instance = new GAVirtualDB();
	
	public static GAVirtualDB get() {
		return instance;
	}
	
	DuplicateMap<Long,GAGame> ownedGames = new DuplicateMap<>();
	
	DuplicateMap<Long,GAGame> activeGames = new DuplicateMap<>();
	
	
	
	public void create( GAGame game ) {
		ownedGames.put( game.getOwner(), game );
		for( Long provider : game.getParticipants() ) {
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
	
}
