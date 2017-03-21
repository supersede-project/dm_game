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

package eu.supersede.gr.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.AHPGamesJpa;
import eu.supersede.gr.jpa.AHPPlayerMovesJpa;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.HAHPPlayerMove;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.gr.utility.PointsLogic;

@RestController
@RequestMapping("/ahprp/playermove")
public class PlayerMoveRest
{
    @Autowired
    private PointsLogic pointsLogic;

    @Autowired
    private AHPGamesJpa games;

    @Autowired
    private UsersJpa users;

    @Autowired
    private AHPPlayerMovesJpa playerMoves;

    @Autowired
    private ValutationCriteriaJpa criterias;

    @Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesData;

    /**
     * Get all the playerMoves of the logged user.
     * @param authentication
     * @param gameId
     * @param criteriaId
     * @param gameNotFinished
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @Transactional
    public List<HAHPPlayerMove> getPlayerMoves(Authentication authentication, @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long criteriaId,
            @RequestParam(defaultValue = "false") Boolean gameNotFinished)
    {

        List<HAHPPlayerMove> moves;

        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        User player = users.findOne(currentUser.getUserId());

        if (gameNotFinished)
        {
            if (gameId != null && criteriaId != null)
            {
                HAHPGame game = games.getOne(gameId);
                ValutationCriteria criteria = criterias.getOne(criteriaId);
                moves = playerMoves.findByPlayerAndGameAndCriteriaAndGameNotFinished(player, game, criteria);
            }
            else if (gameId != null)
            {
                HAHPGame game = games.getOne(gameId);
                moves = playerMoves.findByPlayerAndGameAndGameNotFinished(player, game);
            }
            else if (criteriaId != null)
            {
                ValutationCriteria criteria = criterias.getOne(criteriaId);
                moves = playerMoves.findByPlayerAndCriteriaAndGameNotFinished(player, criteria);
            }
            else
            {
                moves = playerMoves.findByPlayerAndGameNotFinished(player);
            }
        }
        else
        {
            if (gameId != null && criteriaId != null)
            {
                HAHPGame game = games.getOne(gameId);
                ValutationCriteria criteria = criterias.getOne(criteriaId);
                moves = playerMoves.findByPlayerAndGameAndCriteria(player, game, criteria);
            }
            else if (gameId != null)
            {
                HAHPGame game = games.getOne(gameId);
                moves = playerMoves.findByPlayerAndGame(player, game);
            }
            else if (criteriaId != null)
            {
                ValutationCriteria criteria = criterias.getOne(criteriaId);
                moves = playerMoves.findByPlayerAndCriteria(player, criteria);
            }
            else
            {
                moves = playerMoves.findByPlayer(player);
            }
        }

        return moves;
    }

    /**
     * Get a specific playerMove.
     * @param playerMoveId
     */
    @RequestMapping("/{playerMoveId}")
    public HAHPPlayerMove getPlayerMove(@PathVariable Long playerMoveId)
    {
        HAHPPlayerMove playerMove = playerMoves.findOne(playerMoveId);

        if (playerMove == null)
        {
            throw new NotFoundException();
        }

        return playerMove;
    }

    /**
     * Get all the playersMove of a specific requirementMatrixData.
     * @param requirementsMatrixDataId
     * @return
     */
    @RequestMapping("/requirementsmatrixdata/{requirementsMatrixDataId}")
    public List<HAHPPlayerMove> getRequirementsMatrixDataPlayerMove(@PathVariable Long requirementsMatrixDataId)
    {
        HAHPRequirementsMatrixData rmd = requirementsMatricesData.findOne(requirementsMatrixDataId);

        List<HAHPPlayerMove> listPlayerMoves = playerMoves.findByRequirementsMatrixData(rmd);

        return listPlayerMoves;
    }

    /**
     * Set the vote for of a player in his/her PlayerMove.
     * @param authentication
     * @param playerMoveId
     * @param vote
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{playerMoveId}/vote/{vote}")
    public Long setPlayerMoveVote(Authentication authentication, @PathVariable Long playerMoveId,
            @PathVariable Long vote)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        User user = users.findOne(currentUser.getUserId());

        HAHPPlayerMove playerMove = playerMoves.findOne(playerMoveId);
        playerMove.setValue(vote);
        playerMove.setPlayed(true);
        playerMove.setPlayedTime(new Date());
        playerMoves.save(playerMove);

        HAHPRequirementsMatrixData rmd = requirementsMatricesData
                .findOne(playerMove.getRequirementsMatrixData().getRequirementsMatrixDataId());

        Long criteriaId = rmd.getCriteria().getCriteriaId();

        // add points for player vote
        pointsLogic.addPoint(user, -1l, criteriaId);

        return rmd.getGame().getGameId();
    }

    /**
     * Ppen a playerMove.
     * @param playerMoveId
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/open/{playerMoveId}")
    public void openPlayerMove(@PathVariable Long playerMoveId)
    {
        HAHPPlayerMove playerMove = playerMoves.findOne(playerMoveId);
        playerMove.setValue(null);
        playerMove.setPlayedTime(null);
        playerMove.setPlayed(false);
        playerMoves.save(playerMove);
    }

    /**
     * Get all the players of a specific requirmentsMatrixData.
     * @param requirementsMatrixDataId
     * @return
     */
    @RequestMapping("/players/{requirementsMatrixDataId}")
    public List<User> getPlayerMovePlayers(@PathVariable Long requirementsMatrixDataId)
    {
        HAHPRequirementsMatrixData requirementMatrixData = requirementsMatricesData.getOne(requirementsMatrixDataId);

        List<HAHPPlayerMove> listPlayerMoves = playerMoves.findByRequirementsMatrixData(requirementMatrixData);
        List<User> movePlayers = new ArrayList<>();
        for (int i = 0; i < listPlayerMoves.size(); i++)
        {
            movePlayers.add(i, listPlayerMoves.get(i).getPlayer());
        }

        return movePlayers;
    }
}