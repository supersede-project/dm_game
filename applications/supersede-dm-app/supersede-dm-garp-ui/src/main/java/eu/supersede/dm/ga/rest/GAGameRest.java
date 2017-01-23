package eu.supersede.dm.ga.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @RequestMapping(value = "/newgame", method = RequestMethod.POST)
    public void createNewGame(Authentication authentication, String[] gameRequirements, String[] gameCriteria,
            String[] gamePlayers)
    {
        List<Long> requirements = new ArrayList<>();
        List<Long> criteria = new ArrayList<>();
        List<Long> players = new ArrayList<>();

        for (String id : gameRequirements)
        {
            requirements.add(new Long(id));
        }

        for (String id : gameCriteria)
        {
            criteria.add(new Long(id));
        }

        for (String id : gamePlayers)
        {
            players.add(new Long(id));
        }

        GAGameSummary game = new GAGameSummary();
        long currentTime = System.currentTimeMillis();
        game.setId(currentTime);
        game.setOwner(((DatabaseUser) authentication.getPrincipal()).getUserId());

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        game.setDate(sdfDate.format(now));

        game.setStatus("open");

        virtualDb.create(game, criteria, requirements, players);
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
    public List<Long> getGameCriteria(Authentication authentication, Long gameId)
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
        List<Long> criteria = virtualDb.getCriteria(game);
        List<String> gameCriteria = new ArrayList<>();

        for (Long criterionId : criteria)
        {
            gameCriteria.add("" + criterionId);
        }

        algo.setCriteria(gameCriteria);

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