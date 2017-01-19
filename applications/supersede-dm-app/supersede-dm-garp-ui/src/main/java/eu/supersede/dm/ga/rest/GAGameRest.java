package eu.supersede.dm.ga.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import eu.supersede.gr.model.Requirement;

@RestController
@RequestMapping("/garp/game")
public class GAGameRest
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private UsersJpa users;

    @Autowired
    private GAVirtualDB virtualDb;

    @RequestMapping(value = "/ownedgames", method = RequestMethod.GET)
    public List<GAGameSummary> getOwnedGames(Authentication authentication)
    {
        return virtualDb.getOwnedGames(((DatabaseUser) authentication.getPrincipal()).getUserId());
    }

    @RequestMapping(value = "/activegames", method = RequestMethod.GET)
    public List<GAGameSummary> getActiveGames(Authentication authentication)
    {
        return virtualDb.getActiveGames(((DatabaseUser) authentication.getPrincipal()).getUserId());
    }

    @RequestMapping(value = "/newrandom", method = RequestMethod.GET)
    public GAGameSummary createNewRandomGame(Authentication authentication)
    {
        return virtualDb.createRandomGame(((DatabaseUser) authentication.getPrincipal()).getUserId());
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

        virtualDb.setRanking(gameId, userId, reqs);
    }

    @RequestMapping(value = "/requirements", method = RequestMethod.GET)
    public List<Requirement> getRequirements(Authentication authentication, Long gameId, String criterion)
    {
        Long userId = ((DatabaseUser) authentication.getPrincipal()).getUserId();
        List<Long> reqs = virtualDb.getRankingsCriterion(gameId, userId, criterion);
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
        return virtualDb.getRequirements(gameId, ((DatabaseUser) authentication.getPrincipal()).getUserId());
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