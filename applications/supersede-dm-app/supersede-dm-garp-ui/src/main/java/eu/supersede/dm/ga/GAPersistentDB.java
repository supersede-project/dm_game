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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.supersede.dm.methods.GAMethod;
import eu.supersede.gr.data.GAGameDetails;
import eu.supersede.gr.data.GAGameSummary;
import eu.supersede.gr.data.GARole;
import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.GAGameCriteriaJpa;
import eu.supersede.gr.jpa.GAGameParticipationJpa;
import eu.supersede.gr.jpa.GAGameRankingsJpa;
import eu.supersede.gr.jpa.GAGameRequirementJpa;
import eu.supersede.gr.jpa.GAGameSummaryJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HGAGameCriterion;
import eu.supersede.gr.model.HGAGameParticipation;
import eu.supersede.gr.model.HGAGameRequirement;
import eu.supersede.gr.model.HGAGameSummary;
import eu.supersede.gr.model.HGARankingInfo;
import eu.supersede.gr.model.Requirement;

@Component
public class GAPersistentDB
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequirementsJpa requirements;

    @Autowired
    private GAGameSummaryJpa games;

    @Autowired
    private GAGameCriteriaJpa criteriaJpa;

    @Autowired
    private GAGameParticipationJpa participationJpa;

    @Autowired
    private ActivitiesJpa activitiesJpa;

    @Autowired
    private GAGameRequirementJpa gameRequirementsJpa;

    @Autowired
    private GAGameRankingsJpa rankingsJpa;

    public void create(GAGameSummary game, List<Long> criteria, List<Long> requirements, List<Long> participants)
    {
        HActivity activity = new HActivity();
        activity.setMethodName(GAMethod.NAME);
        activitiesJpa.save(activity);

        game.setId(null);
        HGAGameSummary info = new HGAGameSummary(activity.getId(), game);
        games.save(info);

        for (Long cId : criteria)
        {
            HGAGameCriterion c = new HGAGameCriterion(info.getId(), cId);
            criteriaJpa.save(c);
        }

        for (Long rId : requirements)
        {
            Requirement r = this.requirements.findOne(rId);

            if (r == null)
            {
                continue;
            }

            HGAGameRequirement req = new HGAGameRequirement();
            req.setGameId(info.getId());
            req.setRequirementId(r.getRequirementId());
            this.gameRequirementsJpa.save(req);
        }

        for (Long uId : participants)
        {
            HGAGameParticipation p = new HGAGameParticipation();
            p.setGameId(info.getId());
            p.setUserId(uId);
            p.setRole(GARole.OpinionProvider.name());
            participationJpa.save(p);
        }

        // Save owner
        HGAGameParticipation p = new HGAGameParticipation();
        p.setGameId(info.getId());
        p.setUserId(game.getOwner());
        p.setRole(GARole.Supervisor.name());
        participationJpa.save(p);

        log.info("Created game: " + game.getId() + ", requirements: " + requirements.size() + ", criteria: "
                + criteria.size() + ", participants: " + participants.size());
    }

    public List<GAGameSummary> getOwnedGames(Long owner)
    {
        return getGamesByRole(owner, GARole.Supervisor.name());
    }

    public List<GAGameSummary> getActiveGames(Long userId)
    {
        return getGamesByRole(userId, GARole.OpinionProvider.name());
    }

    private List<GAGameSummary> getGamesByRole(Long userId, String roleName)
    {
        List<GAGameSummary> games = new ArrayList<>();
        List<Long> gameList = this.participationJpa.findGames(userId, roleName);

        for (Long gameId : gameList)
        {
            HGAGameSummary info = this.games.findOne(gameId);

            if (info == null)
            {
                continue;
            }

            GAGameSummary summary = extract(info);
            games.add(summary);
        }

        return games;
    }

    private GAGameSummary extract(HGAGameSummary info)
    {
        GAGameSummary summary = new GAGameSummary();
        summary.setId(info.getId());
        summary.setDate(info.getDate());
        summary.setOwner(info.getOwner());
        summary.setStatus(info.getStatus());
        summary.setName(info.getName());
        return summary;
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

        HGARankingInfo ranking = new HGARankingInfo();

        ranking.setUserId(userId);
        ranking.setGameId(gameId);

        String jsonizedRanking = serializeRankings(map);
        ranking.setJsonizedRanking(jsonizedRanking);

        rankingsJpa.save(ranking);
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
        HGAGameSummary info = games.findOne(gameId);

        if (info == null)
        {
            return null;
        }

        GAGameDetails d = new GAGameDetails();

        d.setGame(extract(info));

        // add criteria
        List<Long> criteriaList = this.criteriaJpa.findByGame(info.getId());
        d.setCriteria(criteriaList);

        // add requirements
        List<Long> reqsList = this.gameRequirementsJpa.findRequirementIdsByGame(info.getId());
        d.setRequirements(reqsList);

        // add users
        List<Long> participantsList = this.participationJpa.findParticipants(info.getId(),
                GARole.OpinionProvider.name());
        d.setParticipants(participantsList);

        Map<Long, Map<String, List<Long>>> rankings = new HashMap<>();

        // add rankings
        for (Long userId : participantsList)
        {
            String json = this.rankingsJpa.findRankingByGameAndUser(info.getId(), userId);
            Map<String, List<Long>> map = deserializeRankings(json);
            rankings.put(userId, map);
        }

        d.setRankings(rankings);

        return d;
    }

    private String serializeRankings(Map<String, List<Long>> map)
    {
        return new Gson().toJson(map);
    }

    private Map<String, List<Long>> deserializeRankings(String json)
    {
        Type type = new TypeToken<Map<String, List<Long>>>()
        {
        }.getType();
        return new Gson().fromJson(json, type);
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

    public List<Long> getCriteria(GAGameSummary game)
    {
        return getCriteria(game.getId());
    }

    public List<Long> getCriteria(long gameId)
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

        for (Long c : gi.getCriteria())
        {
            map.put("" + c, gi.getRequirements());
        }

        return map;
    }
}