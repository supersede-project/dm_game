package eu.supersede.dm.ga.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ga.GAVirtualDB;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.data.GAGameSummary;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("/garp/game")
public class GAGameRest
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ValutationCriteriaJpa valutationCriterias;

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private UsersJpa users;

    @Autowired
    private GAVirtualDB virtualDb;

    @RequestMapping(value = "/ownedgames", method = RequestMethod.GET)
    public List<GAGameSummary> getOwnedGames(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        return virtualDb.getOwnedGames(currentUser.getUserId());
    }

    @RequestMapping(value = "/activegames", method = RequestMethod.GET)
    public List<GAGameSummary> getActiveGames(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        return virtualDb.getActiveGames(currentUser.getUserId());
    }

    @RequestMapping(value = "/newrandom", method = RequestMethod.GET)
    public GAGameSummary createNewRandomGame(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        Long userId = currentUser.getUserId();
        GAGameSummary game = new GAGameSummary();
        game.setId(System.currentTimeMillis());
        game.setOwner(userId);
        game.setDate("12/12/2016");
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

        virtualDb.create(game, gameCriteria, gameRequirements, gameParticipants);
        return game;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public void submitAllPriorities(Authentication authentication, @RequestParam Long gameId,
            @RequestBody Map<String, List<Long>> reqs)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();

        for (String key : reqs.keySet())
        {
            log.info("Sending priorities: game " + gameId + " user " + currentUser.getUserId() + " criterion " + key
                    + " reqs " + reqs.get(key));
        }

        virtualDb.setRanking(gameId, currentUser.getUserId(), reqs);
    }

    @RequestMapping(value = "/requirements", method = RequestMethod.GET)
    public List<Requirement> getRequirements(Authentication authentication, Long gameId, String criterion)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        List<Long> reqs = virtualDb.getRankingsCriterion(gameId, currentUser.getUserId(), criterion);
        List<Requirement> requirements = new ArrayList<>();

        if (reqs != null)
        {
            for (Long requirementId : reqs)
            {
                requirements.add(availableRequirements.findOne(requirementId));
            }
        }
        else
        {
            List<Long> tmp = virtualDb.getRequirements(gameId);

            for (Long requirementId : tmp)
            {
                requirements.add(availableRequirements.findOne(requirementId));
            }
        }

        return requirements;
    }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public GAGameSummary getGame(Authentication authentication, Long gameId)
    {
        return virtualDb.getGameInfo(gameId).getGame();
    }

    @RequestMapping(value = "/gamecriteria", method = RequestMethod.GET)
    public List<String> getGameCriteria(Authentication authentication, Long gameId)
    {
        return virtualDb.getCriteria(gameId);
    }

    @RequestMapping(value = "/requirement", method = RequestMethod.GET)
    public Requirement getRequirement(Authentication authentication, Long requirementId)
    {
        return availableRequirements.findOne(requirementId);
    }

    @RequestMapping(value = "/gamerequirements", method = RequestMethod.GET)
    public Map<String, List<Long>> getGameRequirements(Authentication authentication, Long gameId)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        return virtualDb.getRequirements(gameId, currentUser.getUserId());
    }

    @RequestMapping(value = "/calc", method = RequestMethod.GET)
    public List<Map<String, Double>> calcRanking(Authentication authentication, GAGameSummary game)
    {
        IGAAlgorithm algo = new IGAAlgorithm();
        algo.setCriteria(virtualDb.getCriteria(game));

        for (Long rid : virtualDb.getRequirements(game.getId()))
        {
            algo.addRequirement("" + rid, new ArrayList<>());
        }

        // get all the players in this game
        List<Long> participantIds = virtualDb.getParticipants(game);

        // get the rankings of each player for each criterion
        for (Long userId : participantIds)
        {
            String player = users.getOne(userId).getName();
            Map<String, List<Long>> userRanking = virtualDb.getRanking(game.getId(), userId);
            Map<String, List<String>> userRankingStr = new HashMap<>();

            for (Entry<String, List<Long>> entry : userRanking.entrySet())
            {
                userRankingStr.put(entry.getKey(), idToString(entry.getValue()));
            }

            algo.addRanking(player, userRankingStr);
        }

        return algo.calc();
    }

    private List<String> idToString(List<Long> ids)
    {
        List<String> strings = new ArrayList<>();

        for (Long id : ids)
        {
            strings.add(availableRequirements.getOne(id).getName());
        }

        return strings;
    }
}