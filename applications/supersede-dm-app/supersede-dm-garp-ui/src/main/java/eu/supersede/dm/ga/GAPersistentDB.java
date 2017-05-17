/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.supersede.dm.ga;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.PropertyBag;
import eu.supersede.dm.methods.GAMethod;
import eu.supersede.dm.methods.GANegotiatorVotingMethod;
import eu.supersede.dm.methods.GAPlayerVotingMethod;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.GAGameCriteriaJpa;
import eu.supersede.gr.jpa.GAGameParticipationJpa;
import eu.supersede.gr.jpa.GAGameRankingsJpa;
import eu.supersede.gr.jpa.GAGameRequirementJpa;
import eu.supersede.gr.jpa.GAGameSolutionsJpa;
import eu.supersede.gr.jpa.GAGameSummaryJpa;
import eu.supersede.gr.jpa.GAPlayerWeightsJpa;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HGAGameCriterion;
import eu.supersede.gr.model.HGAGameParticipation;
import eu.supersede.gr.model.HGAGameRequirement;
import eu.supersede.gr.model.HGAGameSummary;
import eu.supersede.gr.model.HGAPlayerWeight;
import eu.supersede.gr.model.HGARankingInfo;
import eu.supersede.gr.model.HGASolution;
import eu.supersede.gr.model.Requirement;

@Component
public class GAPersistentDB
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GAGameSummaryJpa gamesJpa;

    @Autowired
    private GAGameCriteriaJpa criteriaJpa;

    @Autowired
    private GAGameParticipationJpa participationJpa;

    @Autowired
    private GAGameRequirementJpa gameRequirementsJpa;

    @Autowired
    private GAGameRankingsJpa rankingsJpa;

    @Autowired
    private GAPlayerWeightsJpa playerWeightsJpa;

    @Autowired
    private GAGameSolutionsJpa solutionsJpa;

    @Autowired
    private ActivitiesJpa activitiesJpa;

    public Long getProcessId(Long activityId)
    {
        HActivity a = DMGame.get().getJpa().activities.findOne(activityId);
        return a.getProcessId();
    }

    public Long getGameId(Long activityId)
    {
        HActivity a = DMGame.get().getJpa().activities.findOne(activityId);
        String ret = DMGame.get().getProcessManager(a.getProcessId()).getProperties(a).get("gameId", "");
        return Long.parseLong(ret);
    }

    public void create(Authentication authentication, String name, Long[] gameRequirements,
            Map<String, Map<String, Double>> playersWeights, Map<String, Double> criteriaWeights,
            Long[] gameOpinionProviders, Long gameNegotiator, Long processId)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        List<Long> requirements = new ArrayList<>();
        List<Long> opinionProviders = new ArrayList<>();

        for (Long requirementId : gameRequirements)
        {
            requirements.add(requirementId);
        }

        for (Long userId : gameOpinionProviders)
        {
            opinionProviders.add(userId);
        }

        HGAGameSummary gameSummary = new HGAGameSummary();
        long currentTime = System.currentTimeMillis();
        gameSummary.setId(currentTime);
        gameSummary.setName(name);
        gameSummary.setOwner(currentUser.getUserId());

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        gameSummary.setDate(sdfDate.format(now));

        gameSummary.setStatus(GAGameStatus.Open.name());
        HActivity persistentActivity = mgr.createActivity(GAMethod.NAME, currentUser.getUserId());

        // temporary workaround to avoid supervisor activities to be shown in home page
        persistentActivity.setUserId(null);
        activitiesJpa.save(persistentActivity);

        gameSummary.setActivityId(persistentActivity.getId());
        HGAGameSummary persistedGameSummary = gamesJpa.save(gameSummary);
        Long gameId = persistedGameSummary.getId();

        for (String criterionId : playersWeights.keySet())
        {
            Map<String, Double> playerCriteriaWeights = playersWeights.get(criterionId);

            for (String userId : playerCriteriaWeights.keySet())
            {
                Double weight = NumberUtils.convertNumberToTargetClass(playerCriteriaWeights.get(userId), Double.class);
                HGAPlayerWeight playerWeight = new HGAPlayerWeight(gameId, new Long(criterionId), new Long(userId),
                        weight);
                playerWeightsJpa.save(playerWeight);
            }
        }

        for (String criterionId : criteriaWeights.keySet())
        {
            HGAGameCriterion criterion = new HGAGameCriterion(gameId, new Long(criterionId),
                    NumberUtils.convertNumberToTargetClass(criteriaWeights.get(criterionId), Double.class));
            criteriaJpa.save(criterion);
        }

        for (Long requirementId : requirements)
        {
            Requirement requirement = DMGame.get().getJpa().requirements.findOne(requirementId);

            if (requirement == null)
            {
                continue;
            }

            HGAGameRequirement gameRequirement = new HGAGameRequirement();
            gameRequirement.setGameId(gameId);
            gameRequirement.setRequirementId(requirement.getRequirementId());
            gameRequirementsJpa.save(gameRequirement);
        }

        for (Long userId : opinionProviders)
        {
            HGAGameParticipation p = new HGAGameParticipation();
            p.setGameId(gameId);
            p.setUserId(userId);
            p.setRole(GARole.OpinionProvider.name());
            participationJpa.save(p);
        }

        // Save negotiator
        HGAGameParticipation gameParticipation = new HGAGameParticipation();
        gameParticipation.setGameId(gameId);
        gameParticipation.setUserId(gameNegotiator);
        gameParticipation.setRole(GARole.Negotiator.name());
        participationJpa.save(gameParticipation);

        // Save owner
        gameParticipation = new HGAGameParticipation();
        gameParticipation.setGameId(gameId);
        gameParticipation.setUserId(gameSummary.getOwner());
        gameParticipation.setRole(GARole.Supervisor.name());
        participationJpa.save(gameParticipation);

        if (processId != -1)
        {
            createActivities(processId, gameId);
        }
    }

    private void createActivities(Long processId, Long gameId)
    {
        GAGameDetails gameDetails = getGameInfo(gameId);
        ProcessManager mgr = DMGame.get().getProcessManager(processId);

        for (Long userId : gameDetails.getOpinionProviders())
        {
            HActivity a = mgr.createActivity(GAPlayerVotingMethod.NAME, userId);
            PropertyBag bag = mgr.getProperties(a);
            bag.set("gameId", "" + gameId);
        }

        for (Long userId : gameDetails.getNegotiators())
        {
            HActivity a = mgr.createActivity(GANegotiatorVotingMethod.NAME, userId);
            PropertyBag bag = mgr.getProperties(a);
            bag.set("gameId", "" + gameId);
        }
    }

    public List<HGAGameSummary> getGamesByRoleAndProcess(Long userId, String roleName, Long processId)
    {
        List<HGAGameSummary> games = new ArrayList<>();
        List<Long> gameList = participationJpa.findGames(userId, roleName);

        for (Long gameId : gameList)
        {
            HGAGameSummary info = gamesJpa.findOne(gameId);

            // FIXME: in case of game deletion, some participations may be left behind
            if (info == null)
            {
                // Game not found
                continue;
            }

            HActivity activity = activitiesJpa.findOne(info.getActivityId());

            // FIXME: in case of game deletion, some activities may be left behind
            if (activity == null)
            {
                continue;
            }

            if (activity.getProcessId() != null && activity.getProcessId().equals(processId.longValue()))
            {
                HGAGameSummary summary = extract(info);
                games.add(summary);
            }
        }

        return games;
    }

    private HGAGameSummary extract(HGAGameSummary info)
    {
        HGAGameSummary summary = new HGAGameSummary();
        summary.setId(info.getId());
        summary.setDate(info.getDate());
        summary.setOwner(info.getOwner());
        summary.setStatus(info.getStatus());
        summary.setName(info.getName());
        return summary;
    }

    public void setRanking(Long gameId, Long userId, Map<Long, List<Long>> reqs)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return;
        }

        Map<Long, List<Long>> map = gi.getRankings().get(userId);

        if (map == null)
        {
            map = new HashMap<>();
            gi.getRankings().put(userId, map);
        }

        for (Long key : reqs.keySet())
        {
            map.put(key, reqs.get(key));
        }

        HGARankingInfo ranking = new HGARankingInfo();

        ranking.setUserId(userId);
        ranking.setGameId(gameId);

        String jsonizedRanking = serializeRankings(map);
        ranking.setJsonizedRanking(jsonizedRanking);

        rankingsJpa.save(ranking);
    }

    public List<Long> getRankingsCriterion(Long gameId, Long userId, Long criterion)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        Map<Long, List<Long>> map = gi.getRankings().get(userId);

        if (map == null || map.get(criterion) == null)
        {
            return new ArrayList<>();
        }

        return map.get(criterion);
    }

    public Map<Long, List<Long>> getRanking(Long gameId, Long userId)
    {
        GAGameDetails gi = getGameInfo(gameId);
        Map<Long, List<Long>> userRanking = gi.getRankings().get(userId);

        if (userRanking == null)
        {
            return new HashMap<>();
        }
        else
        {
            return userRanking;
        }
    }

    public void selectSolution(Long gameId, List<Long> solution)
    {
        String jsonSolution = serializeSolution(solution);
        HGASolution gaSolution = new HGASolution();
        gaSolution.setGameId(gameId);
        gaSolution.setJsonizedSolution(jsonSolution);
        solutionsJpa.save(gaSolution);
    }

    public List<Long> getSolution(Long gameId)
    {
        HGASolution gaSolution = solutionsJpa.findByGameId(gameId);

        if (gaSolution == null)
        {
            return new ArrayList<>();
        }

        String jsonSolution = gaSolution.getJsonizedSolution();
        return deserializeSolution(jsonSolution);
    }

    public GAGameDetails getGameInfo(Long gameId)
    {
        HGAGameSummary gameInfo = gamesJpa.findOne(gameId);

        if (gameInfo == null)
        {
            return null;
        }

        GAGameDetails d = new GAGameDetails();

        d.setGame(extract(gameInfo));

        // add requirements
        List<Long> gameRequirements = gameRequirementsJpa.findRequirementIdsByGame(gameInfo.getId());
        d.setRequirements(gameRequirements);

        // add opinion providers
        List<Long> opinionProviders = participationJpa.findParticipants(gameInfo.getId(),
                GARole.OpinionProvider.name());
        d.setOpinionProviders(opinionProviders);

        // add negotiators
        List<Long> negotiators = participationJpa.findParticipants(gameInfo.getId(), GARole.Negotiator.name());
        d.setNegotiators(negotiators);

        // add criteria weights
        List<Long> gameCriteria = criteriaJpa.findCriteriaByGame(gameInfo.getId());
        HashMap<Long, Double> criteriaWeights = new HashMap<>();

        for (Long criterionId : gameCriteria)
        {
            criteriaWeights.put(criterionId, criteriaJpa.findCriterionWeightByGame(gameInfo.getId(), criterionId));
        }

        d.setCriteriaWeights(criteriaWeights);

        // add player weights
        Map<Long, Map<Long, Double>> playerWeights = new HashMap<>();

        for (Long criterionId : gameCriteria)
        {
            Map<Long, Double> criterionPlayerWeights = new HashMap<>();

            for (Long userId : opinionProviders)
            {
                Double weight = playerWeightsJpa.findPlayerWeightByGameAndCriterion(gameId, criterionId, userId);
                criterionPlayerWeights.put(userId, weight);
            }

            playerWeights.put(criterionId, criterionPlayerWeights);
        }

        d.setPlayerWeights(playerWeights);

        Map<Long, Map<Long, List<Long>>> rankings = new HashMap<>();

        // add rankings
        for (Long userId : opinionProviders)
        {
            String json = rankingsJpa.findRankingByGameAndUser(gameInfo.getId(), userId);

            if (json != null && !json.equals(""))
            {
                Map<Long, List<Long>> map = deserializeRankings(json);
                rankings.put(userId, map);
            }
        }

        d.setRankings(rankings);

        return d;
    }

    public void closeGame(Long gameId, Long processId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        List<HActivity> opinionProvidersActivities = mgr.getOngoingActivities(GAPlayerVotingMethod.NAME);
        List<HActivity> negotiatorsActivities = mgr.getOngoingActivities(GANegotiatorVotingMethod.NAME);

        for (HActivity a : opinionProvidersActivities)
        {
            PropertyBag bag = mgr.getProperties(a);
            Long ongoingGame = Long.parseLong(bag.get("gameId", "0"));

            if (!ongoingGame.equals(gameId))
            {
                // Found property of another game
                continue;
            }

            mgr.deleteActivity(a);
        }

        for (HActivity a : negotiatorsActivities)
        {
            PropertyBag bag = mgr.getProperties(a);
            Long ongoingGame = Long.parseLong(bag.get("gameId", "0"));

            if (!ongoingGame.equals(gameId))
            {
                // Found property of another game
                continue;
            }

            mgr.deleteActivity(a);
        }

        HGAGameSummary gameInfo = gamesJpa.findOne(gameId);
        gameInfo.setStatus(GAGameStatus.Closed.name());
        gamesJpa.save(gameInfo);
    }

    public void openGame(Long processId, Long gameId)
    {
        HGAGameSummary gameInfo = gamesJpa.findOne(gameId);
        gameInfo.setStatus(GAGameStatus.Open.name());
        gamesJpa.save(gameInfo);
        createActivities(processId, gameId);
    }

    private String serializeRankings(Map<Long, List<Long>> map)
    {
        return new Gson().toJson(map);
    }

    private Map<Long, List<Long>> deserializeRankings(String json)
    {
        Type type = new TypeToken<Map<Long, List<Long>>>()
        {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    private String serializeSolution(List<Long> solution)
    {
        return new Gson().toJson(solution);
    }

    private List<Long> deserializeSolution(String json)
    {
        Type type = new TypeToken<List<Long>>()
        {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public List<Long> getOpinionProviders(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new ArrayList<>();
        }

        return gi.getOpinionProviders();
    }

    public Map<Long, Map<Long, Double>> getPlayerWeights(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new HashMap<>();
        }

        return gi.getPlayerWeights();
    }

    public Map<Long, Double> getCriteriaWeights(Long gameId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            throw new NotFoundException("Unable to find game with id " + gameId);
        }

        return gi.getCriteriaWeights();
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

    public Map<Long, List<Long>> getRequirements(Long gameId, Long userId)
    {
        GAGameDetails gi = getGameInfo(gameId);

        if (gi == null)
        {
            return new HashMap<>();
        }

        Map<Long, List<Long>> map = new HashMap<>();

        for (Long c : gi.getCriteriaWeights().keySet())
        {
            map.put(c, gi.getRequirements());
        }

        return map;
    }
}