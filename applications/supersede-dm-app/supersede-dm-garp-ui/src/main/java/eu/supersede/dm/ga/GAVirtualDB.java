package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.dm.ga.data.GAGameDetails;
import eu.supersede.dm.ga.data.GAGameSummary;
import eu.supersede.dm.ga.db.HAttribute;
import eu.supersede.dm.ga.db.HEntity;
import eu.supersede.dm.ga.db.HGAGameSummary;
import eu.supersede.dm.ga.jpa.AttributesJpa;
import eu.supersede.dm.ga.jpa.EntitiesJpa;
import eu.supersede.dm.ga.jpa.GAGameSummaryJpa;

@Component
public class GAVirtualDB implements IGADataview
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static IGADataview instance = new GAVirtualDB();

    public static IGADataview get()
    {
        return instance;
    }

    DuplicateMap<Long, GAGameSummary> ownedGames = new DuplicateMap<>();
    DuplicateMap<Long, GAGameSummary> activeGames = new DuplicateMap<>();
    Map<Long, GAGameDetails> games = new HashMap<>();
    
//    @Autowired private EntitiesJpa				entities;
//    
//    @Autowired private AttributesJpa			attributes;
    
    @Autowired private GAGameSummaryJpa			gameSummaries;
    
    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#create(eu.supersede.dm.ga.data.GAGame)
	 */
    @Override
	public GAGameDetails create(GAGameSummary game)
    {
        GAGameDetails gi = new GAGameDetails();
        gi.setGame( game );
        games.put(game.getId(), gi);
        ownedGames.put(game.getOwner(), game);
        
        HGAGameSummary gs = new HGAGameSummary( game );
        gameSummaries.save( gs );
        
//        {
//            HEntity entity = null;
//            
//            
//            
//            entity = new HEntity();
//            entity.setClsName( "GameSummary" );
//            entities.save( entity );
//            
//            setAttr( entity, "status", game.getStatus() );
//            setAttr( entity, "date", game.getDate() );
//            setAttr( entity, "orwner", "" + game.getOwner() );
//            
//            
//            
//            entity = new HEntity();
//            entity.setClsName( "GameDetail");
//            entities.save( entity );
//            
//            
//            
//            
//            
//            
//        }
        
        return gi;
    }
    
//	private void setAttr( HEntity entity, String name, String value ) {
//        HAttribute attr = new HAttribute();
//        attr.setEntityId( entity.getId() );
//        attr.setName( name );
//        attr.setValue( value );
//        attributes.save( attr );
//	}
	
    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#create(eu.supersede.dm.ga.data.GAGame, java.util.List, java.util.List, java.util.List)
	 */
    @Override
	public void create(GAGameSummary game, List<String> criteria, List<Long> requirements, List<Long> participants)
    {
        GAGameDetails gi = create(game);
        gi.setRequirements( requirements );
        gi.setCriteria( criteria );
        gi.setParticipants( participants );

        for (Long provider : participants)
        {
            activeGames.put(provider, game);
        }

        log.info(
        		"Created game: " + game.getId() + 
        		", requirements: " + gi.getRequirements().size() + 
        		", criteria: " + gi.getCriteria().size() + 
        		", participants: " + gi.getParticipants().size());
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getOwnedGames(java.lang.Long)
	 */
    @Override
	public List<GAGameSummary> getOwnedGames(Long owner)
    {
        return ownedGames.getList(owner);
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getActiveGames(java.lang.Long)
	 */
    @Override
	public List<GAGameSummary> getActiveGames(Long userId)
    {
        return activeGames.getList(userId);
    }

//    /* (non-Javadoc)
//	 * @see eu.supersede.dm.ga.IGADataview#getActiveGame(java.lang.Long)
//	 */
//    @Override
//	public GAGameSummary getActiveGame(Long userId)
//    {
//        return activeGames.get(userId);
//    }

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

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#setRanking(java.lang.Long, java.lang.Long, java.util.Map)
	 */
    @Override
	public void setRanking(Long gameId, Long userId, Map<String,List<Long>> reqs)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return;
        }
        
        Map<String,List<Long>> map = gi.getRankings().get( userId );
        
        if (map == null)
        {
        	map = new HashMap<>();
        	gi.getRankings().put(userId, map);
        }
        
        for( String key : reqs.keySet() ) {
            map.put( key,  reqs.get( key ) );
        }
        
//        gi.rankings.put(userId, reqs);
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getRankingsCriterion(java.lang.Long, java.lang.Long, java.lang.String)
	 */
    @Override
	public List<Long> getRankingsCriterion(Long gameId, Long userId, String criterion)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return null;
        }
        Map<String, List<Long>> map = gi.getRankings().get(userId);
        if ( map == null){
        	return null;
        }

        return map.get(criterion);
    }
    
    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getRanking(java.lang.Long, java.lang.Long)
	 */
    @Override
	public Map<String,List<Long>> getRanking (Long gameId, Long userId){
    	GAGameDetails gi = getGameInfo(gameId);
    	return gi.getRankings().get(userId);
    }

    
    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getGameInfo(java.lang.Long)
	 */
    @Override
	public GAGameDetails getGameInfo(Long gameId)
    {
        return games.get(gameId);
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getParticipants(eu.supersede.dm.ga.data.GAGame)
	 */
    @Override
	public List<Long> getParticipants(GAGameSummary game)
    {
        GAGameDetails gi = getGameInfo(game.getId());

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getParticipants(java.lang.Long)
	 */
    @Override
	public List<Long> getParticipants(Long gameId){
    	GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();

    }
    
    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getCriteria(eu.supersede.dm.ga.data.GAGame)
	 */
    @Override
	public List<String> getCriteria(GAGameSummary game)
    {
    	return getCriteria( game.getId() );
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getCriteria(long)
	 */
    @Override
	public List<String> getCriteria( long gameId )
    {
        GAGameDetails gi = getGameInfo( gameId );

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getCriteria();
    }

    /* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getRequirements(java.lang.Long)
	 */
    @Override
	public List<Long> getRequirements(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getRequirements();
    }

	/* (non-Javadoc)
	 * @see eu.supersede.dm.ga.IGADataview#getRequirements(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Map<String,List<Long>> getRequirements( Long gameId, Long userId ) {
		GAGameDetails gi = getGameInfo( gameId );
		if( gi == null ) {
			return new HashMap<>();
		}
		Map<String,List<Long>> map = new HashMap<>();
		for( String c : gi.getCriteria() ) {
			map.put( c, gi.getRequirements() );
		}
		return map;
	}
}