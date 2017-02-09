package eu.supersede.dm.ga.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ga.GAPersistentDB;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.data.GAGameSummary;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("/garp/game")
public class GAGameRest
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private ValutationCriteriaJpa availableCriteria;

    @Autowired
    private UsersJpa users;

    @Autowired
    private GAPersistentDB persistentDB;

    @RequestMapping(value = "/ownedgames", method = RequestMethod.GET)
    public List<GAGameSummary> getOwnedGames(Authentication authentication)
    {
        return persistentDB.getOwnedGames(((DatabaseUser) authentication.getPrincipal()).getUserId());
    }

    @RequestMapping(value = "/activegames", method = RequestMethod.GET)
    public List<GAGameSummary> getActiveGames(Authentication authentication)
    {
        return persistentDB.getActiveGames(((DatabaseUser) authentication.getPrincipal()).getUserId());
    }

    @RequestMapping(value = "/newgame", method = RequestMethod.POST)
    public void createNewGame(Authentication authentication, @RequestParam Long[] gameRequirements,
            @RequestBody Map<Long, Double> gameCriteriaWeights, @RequestParam Long[] gamePlayers)
    {
        List<Long> requirements = new ArrayList<>();
        HashMap<Long, Double> criteriaWeights = new HashMap<>();
        List<Long> players = new ArrayList<>();

        for (Long id : gameRequirements)
        {
            requirements.add(id);
        }

        System.out.println("criteria weights: " + gameCriteriaWeights);
        System.out.println("criteria weights size: " + gameCriteriaWeights.size());

        for (Long id : gameCriteriaWeights.keySet())
        {
            System.out.println("id: " + id);
            System.out.println("weight: " + gameCriteriaWeights.get(id));
            criteriaWeights.put(id, gameCriteriaWeights.get(id));
        }

        for (Long id : gamePlayers)
        {
            players.add(id);
        }

        GAGameSummary game = new GAGameSummary();
        long currentTime = System.currentTimeMillis();
        game.setId(currentTime);
        game.setOwner(((DatabaseUser) authentication.getPrincipal()).getUserId());

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        game.setDate(sdfDate.format(now));

        game.setStatus("open");

        persistentDB.create(game, criteriaWeights, requirements, players);
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public void submitAllPriorities(Authentication authentication, @RequestParam Long gameId,
            @RequestBody Map<String, List<Long>> reqs)
    {
        Long userId = ((DatabaseUser) authentication.getPrincipal()).getUserId();

        for (String key : reqs.keySet())
        {
            log.info("Sending priorities: game " + gameId + " user " + userId + " criterion " + key + " reqs "
                    + reqs.get(key));
        }

        persistentDB.setRanking(gameId, userId, reqs);
    }

    @RequestMapping(value = "/requirements", method = RequestMethod.GET)
    public List<Requirement> getRequirements(Authentication authentication, Long gameId, String criterion)
    {
        Long userId = ((DatabaseUser) authentication.getPrincipal()).getUserId();
        List<Long> reqs = persistentDB.getRankingsCriterion(gameId, userId, criterion);
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
            List<Long> tmp = persistentDB.getRequirements(gameId);

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
        return persistentDB.getGameInfo(gameId).getGame();
    }

    @RequestMapping(value = "/gamecriteria", method = RequestMethod.GET)
    public List<ValutationCriteria> getGameCriteria(Authentication authentication, Long gameId)
    {
        List<ValutationCriteria> criteria = new ArrayList<>();
        Set<Long> criteriaIds = persistentDB.getCriteriaWeights(gameId).keySet();

        for (Long criterionId : criteriaIds)
        {
            criteria.add(availableCriteria.findOne(criterionId));
        }

        return criteria;
    }

    @RequestMapping(value = "/requirement", method = RequestMethod.GET)
    public Requirement getRequirement(Authentication authentication, Long requirementId)
    {
        return availableRequirements.findOne(requirementId);
    }

    @RequestMapping(value = "/gamerequirements", method = RequestMethod.GET)
    public Map<String, List<Long>> getGameRequirements(Authentication authentication, Long gameId)
    {
        return persistentDB.getRequirements(gameId, ((DatabaseUser) authentication.getPrincipal()).getUserId());
    }

    @RequestMapping(value = "/calc", method = RequestMethod.GET)
    public List<Map<String, Double>> calcRanking(Authentication authentication, GAGameSummary game)
    {
        IGAAlgorithm algo = new IGAAlgorithm();
        HashMap<Long, Double> criteriaWeights = persistentDB.getCriteriaWeights(game);
        List<String> gameCriteria = new ArrayList<>();

        for (Long criterionId : criteriaWeights.keySet())
        {
            gameCriteria.add("" + criterionId);
            algo.setCriterionWeight("" + criterionId, criteriaWeights.get(criterionId));
        }

        algo.setCriteria(gameCriteria);

        for (Long rid : persistentDB.getRequirements(game.getId()))
        {
            algo.addRequirement("" + rid, new ArrayList<>());
        }

        // get all the players in this game
        List<Long> participantIds = persistentDB.getParticipants(game);

        // get the rankings of each player for each criterion
        for (Long userId : participantIds)
        {
            String player = users.getOne(userId).getName();
            Map<String, List<Long>> userRanking = persistentDB.getRanking(game.getId(), userId);
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