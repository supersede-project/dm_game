package eu.supersede.dm.ga;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.gr.data.GAGameDetails;
import eu.supersede.gr.data.GAGameSummary;
import eu.supersede.gr.jpa.EntitiesJpa;
import eu.supersede.gr.jpa.GAGameSummaryJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HEntity;
import eu.supersede.gr.model.HGAGameSummary;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Component
public class GAVirtualDB
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    DuplicateMap<Long, GAGameSummary> ownedGames = new DuplicateMap<>();
    DuplicateMap<Long, GAGameSummary> activeGames = new DuplicateMap<>();
    Map<Long, GAGameDetails> games = new HashMap<>();

    @Autowired
    private EntitiesJpa entities;

    @Autowired
    private ValutationCriteriaJpa valutationCriterias;

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private UsersJpa users;

    //
    // @Autowired private AttributesJpa attributes;

    @Autowired
    private GAGameSummaryJpa gameSummaries;

    public GAGameSummary createRandomGame(Long userId)
    {
        GAGameSummary game = new GAGameSummary();
        long currentTime = System.currentTimeMillis();
        game.setId(currentTime);
        game.setOwner(userId);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        game.setDate(sdfDate.format(now));

        game.setStatus("open");

        List<String> gameCriteria = new ArrayList<>();
        List<ValutationCriteria> criteria = valutationCriterias.findAll();
        Collections.shuffle(criteria, new Random(System.nanoTime()));

        for (int i = 0; i < Math.min(2, criteria.size()); i++)
        {
            gameCriteria.add(criteria.get(i).getName());
        }

        List<Long> gameRequirements = new ArrayList<>();
        List<Requirement> requirements = availableRequirements.findAll();
        Collections.shuffle(requirements, new Random(System.nanoTime()));
        int max = new Random(System.currentTimeMillis()).nextInt(requirements.size());

        for (int i = 0; i < max; i++)
        {
            gameRequirements.add(requirements.get(i).getRequirementId());
        }

        List<Long> gameParticipants = new ArrayList<>();
        List<User> participants = users.findAll();
        Collections.shuffle(participants, new Random(System.nanoTime()));
        max = new Random(System.currentTimeMillis()).nextInt(participants.size());

        for (int i = 0; i < max; i++)
        {
            gameParticipants.add(participants.get(i).getUserId());
            log.info("Added user id " + participants.get(i).getUserId() + " to game id: " + game.getId());
        }

        create(game, gameCriteria, gameRequirements, gameParticipants);
        return game;
    }

    private GAGameDetails create(GAGameSummary game)
    {
        GAGameDetails gi = new GAGameDetails();
        gi.setGame(game);
        games.put(game.getId(), gi);
        ownedGames.put(game.getOwner(), game);

        HGAGameSummary gs = new HGAGameSummary(game);
        gameSummaries.save(gs);

        // {
        // HEntity entity = null;
        //
        //
        //
        // entity = new HEntity();
        // entity.setClsName( "GameSummary" );
        // entities.save( entity );
        //
        // setAttr( entity, "status", game.getStatus() );
        // setAttr( entity, "date", game.getDate() );
        // setAttr( entity, "orwner", "" + game.getOwner() );
        //
        //
        //
        // entity = new HEntity();
        // entity.setClsName( "GameDetail");
        // entities.save( entity );
        //
        //
        //
        //
        //
        //
        // }

        return gi;
    }

    // private void setAttr( HEntity entity, String name, String value ) {
    // HAttribute attr = new HAttribute();
    // attr.setEntityId( entity.getId() );
    // attr.setName( name );
    // attr.setValue( value );
    // attributes.save( attr );
    // }

    private void setAttr(Long entityId, String name, String value)
    {
        // HAttribute attr = new HAttribute();
        // attr.setEntityId( entityId );
        // attr.setName( name );
        // attr.setValue( value );
        // attributes.save( attr );
    }

    private Long store(Object o)
    {
        HEntity entity = new HEntity();
        entities.save(entity);
        storeAttributes(entity.getId(), o);
        return entity.getId();
    }

    private void storeAttributes(Long id, Object o)
    {
        Field[] fields = o.getClass().getFields();

        for (Field field : fields)
        {
            try
            {
                if (field.isSynthetic())
                {

                }
                else if (field.getType().isArray())
                {

                }
                else
                {

                }

                Object value = field.get(o);

                if (value == null)
                {
                    value = "";
                }
                setAttr(id, field.getName(), value.toString());

            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void create(GAGameSummary game, List<String> criteria, List<Long> requirements, List<Long> participants)
    {
        GAGameDetails gi = create(game);
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

    public List<GAGameSummary> getOwnedGames(Long owner)
    {
        return ownedGames.getList(owner);
    }

    public List<GAGameSummary> getActiveGames(Long userId)
    {
        return activeGames.getList(userId);
    }

    public void setRanking(Long gameId, Long userId, Map<String, List<Long>> reqs)
    {
        GAGameDetails gi = getGameInfo(gameId);

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
        GAGameDetails gi = getGameInfo(gameId);

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
        GAGameDetails gi = getGameInfo(gameId);
        return gi.getRankings().get(userId);
    }

    public GAGameDetails getGameInfo(Long gameId)
    {
        return games.get(gameId);
    }

    public List<Long> getParticipants(GAGameSummary game)
    {
        GAGameDetails gi = getGameInfo(game.getId());

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();
    }

    public List<Long> getParticipants(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getParticipants();
    }

    public List<String> getCriteria(GAGameSummary game)
    {
        return getCriteria(game.getId());
    }

    public List<String> getCriteria(long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getCriteria();
    }

    public List<Long> getRequirements(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getRequirements();
    }

    public Map<String, List<Long>> getRequirements(Long gameId, Long userId)
    {
        GAGameDetails gi = getGameInfo(gameId);

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