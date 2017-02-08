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

/**
* @author Andrea Sosi
**/

package eu.supersede.gr.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.gr.jpa.AHPCriteriasMatricesDataJpa;
import eu.supersede.gr.jpa.AHPGamesJpa;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.GamesPlayersPointsJpa;
import eu.supersede.gr.jpa.PointsJpa;
import eu.supersede.gr.jpa.UserCriteriaPointsJpa;
import eu.supersede.gr.jpa.UserPointsJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAHPCriteriasMatrixData;
import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.GamePlayerPoint;
import eu.supersede.gr.model.HAHPPlayerMove;
import eu.supersede.gr.model.Point;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.UserCriteriaPoint;
import eu.supersede.gr.model.UserPoint;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.gr.rest.AHPRest;

@Component
public class PointsLogic
{
    @Autowired
    private UserPointsJpa userPoints;

    @Autowired
    private PointsJpa points;

    @Autowired
    private ValutationCriteriaJpa criterias;

    @Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;

    @Autowired
    private GamesPlayersPointsJpa gamesPlayersPointsRepository;

    @Autowired
    private AHPCriteriasMatricesDataJpa criteriaMatricesRepository;

    @Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesRepository;

    @Autowired
    private AHPGamesJpa gamesRepository;

    private Double M = 20.0;

    public void addPoint(User user, Long pointId, Long criteriaId)
    {
        Point point = points.findOne(pointId);

        if (criteriaId != -1)
        {
            ValutationCriteria criteria = criterias.findOne(criteriaId);
            UserCriteriaPoint ucp = userCriteriaPoints.findByValutationCriteriaAndUser(criteria, user);

            if (ucp == null)
            {
                UserCriteriaPoint newUcp = new UserCriteriaPoint();

                newUcp.setUser(user);
                newUcp.setValutationCriteria(criteria);
                newUcp.setPoints(point.getCriteriaPoints());
                userCriteriaPoints.save(newUcp);
            }
            else
            {
                ucp.setPoints(ucp.getPoints() + point.getCriteriaPoints());
                userCriteriaPoints.save(ucp);
            }
        }

        UserPoint up = userPoints.findByUser(user);

        if (up == null)
        {
            UserPoint newUp = new UserPoint();

            newUp.setUser(user);
            newUp.setUserPoints(point.getGlobalPoints());
            userPoints.save(newUp);
        }
        else
        {
            up.setUserPoints(up.getUserPoints() + point.getGlobalPoints());
            userPoints.save(up);
        }

        computePoints();
    }

    private void computePoints()
    {
        List<GamePlayerPoint> gamesPlayersPoints = gamesPlayersPointsRepository.findAll();

        // cycle on every gamesPlayersPoints
        for (int i = 0; i < gamesPlayersPoints.size(); i++)
        {
            HAHPGame g = gamesRepository.findOne(gamesPlayersPoints.get(i).getGame().getGameId());

            // set currentPlayer that is used for other methods
            g.setCurrentPlayer(gamesPlayersPoints.get(i).getUser());

            List<HAHPCriteriasMatrixData> criteriasMatrixDataList = criteriaMatricesRepository.findByGame(g);

            // calculate the agreementIndex for every gamesPlayersPoints of a game and a specific user

            Map<String, Double> resultTotal = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(),
                    criteriasMatrixDataList, g.getRequirementsMatrixData());
            Map<String, Double> resultPersonal = AHPRest.CalculatePersonalAHP(
                    gamesPlayersPoints.get(i).getUser().getUserId(), g.getCriterias(), g.getRequirements(),
                    criteriasMatrixDataList, g.getRequirementsMatrixData());
            List<Requirement> gameRequirements = g.getRequirements();
            Double sum = 0.0;

            for (int j = 0; j < resultTotal.size(); j++)
            {
                Double requirementValueTotal = resultTotal.get(gameRequirements.get(j).getRequirementId().toString());
                Double requirementValuePersonal = resultPersonal
                        .get(gameRequirements.get(j).getRequirementId().toString());
                sum = sum
                        + (Math.abs(requirementValueTotal - requirementValuePersonal) * (1.0 - requirementValueTotal));
            }

            Double agreementIndex = M - (M * sum);
            gamesPlayersPoints.get(i).setAgreementIndex(agreementIndex.longValue());

            // calculate the positionInVoting for every gamesPlayersPoints of a game and a specific user

            List<User> players = g.getPlayers();
            List<HAHPRequirementsMatrixData> lrmd = requirementsMatricesRepository.findByGame(g);
            Map<User, Float> gamePlayerVotes = new HashMap<>();

            for (User player : players)
            {
                Integer total = 0;
                Integer voted = 0;

                if (lrmd != null)
                {
                    for (HAHPRequirementsMatrixData data : lrmd)
                    {
                        for (HAHPPlayerMove pm : data.getPlayerMoves())
                        {
                            if (pm.getPlayer().getUserId().equals(player.getUserId()))
                            {
                                total++;

                                if (pm.getPlayed() == true && pm.getValue() != null && !pm.getValue().equals(-1l))
                                {
                                    voted++;
                                }
                            }
                        }
                    }
                }

                gamePlayerVotes.put(player, total.equals(0) ? 0f : ((new Float(voted) / new Float(total)) * 100));
            }

            LinkedHashMap<User, Float> orderedList = sortHashMapByValues(gamePlayerVotes);
            List<User> indexes = new ArrayList<>(orderedList.keySet());
            Integer index = indexes.indexOf(gamesPlayersPoints.get(i).getUser());
            Double positionInVoting = (orderedList.size() - (new Double(index) + 1.0)) + 1.0;
            gamesPlayersPoints.get(i).setPositionInVoting(positionInVoting.longValue());

            // calculate the virtualPosition of a user base on his/her points in a particular game

            GamePlayerPoint gpp = gamesPlayersPointsRepository.findByUserAndGame(gamesPlayersPoints.get(i).getUser(),
                    g);
            List<GamePlayerPoint> specificGamePlayersPoints = gamesPlayersPointsRepository.findByGame(g);

            Collections.sort(specificGamePlayersPoints, new CustomComparator());

            Long virtualPosition = specificGamePlayersPoints.indexOf(gpp) + 1l;
            gamesPlayersPoints.get(i).setVirtualPosition(virtualPosition);

            Long movesPoints = 0l;
            Long gameProgressPoints = 0l;
            Long positionInVotingPoints = 0l;
            Long gameStatusPoints = 0l;
            Long agreementIndexPoints = 0l;
            Long totalPoints = 0l;

            // set the movesPoints
            movesPoints = g.getMovesDone().longValue();

            // setGameProgressPoints
            gameProgressPoints = (long) Math.floor(g.getPlayerProgress() / 10);

            // setPositionInVotingPoints
            if (positionInVoting == 1)
            {
                positionInVotingPoints = 5l;
            }
            else if (positionInVoting == 2)
            {
                positionInVotingPoints = 3l;
            }
            else if (positionInVoting == 3)
            {
                positionInVotingPoints = 2l;
            }

            // setGameStatusPoints
            if (g.getPlayerProgress() != 100)
            {
                gameStatusPoints = -20l;
            }
            else
            {
                gameStatusPoints = 0l;
            }

            // set AgreementIndexPoints
            agreementIndexPoints = agreementIndex.longValue();
            totalPoints = movesPoints.longValue() + gameProgressPoints + positionInVotingPoints + gameStatusPoints
                    + agreementIndexPoints;

            // set totalPoints 0 if the totalPoints are negative
            if (totalPoints < 0)
            {
                totalPoints = 0l;
            }

            gamesPlayersPoints.get(i).setPoints(totalPoints);
            gamesPlayersPointsRepository.save(gamesPlayersPoints.get(i));
        }

        System.out.println("Finished computing votes");
    }

    /**
     * Sort a map by values (in this case the number of moves).
     * @param gamePlayerVotes
     * @return
     */
    private LinkedHashMap<User, Float> sortHashMapByValues(Map<User, Float> gamePlayerVotes)
    {
        List<User> mapKeys = new ArrayList<>(gamePlayerVotes.keySet());
        List<Float> mapValues = new ArrayList<>(gamePlayerVotes.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys, new UserComparator());

        LinkedHashMap<User, Float> sortedMap = new LinkedHashMap<>();

        Iterator<Float> valueIt = mapValues.iterator();

        while (valueIt.hasNext())
        {
            Float val = valueIt.next();
            Iterator<User> keyIt = mapKeys.iterator();

            while (keyIt.hasNext())
            {
                User key = keyIt.next();
                Float comp1 = gamePlayerVotes.get(key);
                Float comp2 = val;

                if (comp1.equals(comp2))
                {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }

        return sortedMap;
    }
}