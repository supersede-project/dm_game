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

package demo.rest;

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

import demo.jpa.GamesJpa;
import demo.jpa.PlayerMovesJpa;
import demo.jpa.RequirementsMatricesDataJpa;
import demo.jpa.UsersJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.Game;
import demo.model.PlayerMove;
import demo.model.RequirementsMatrixData;
import demo.model.User;
import demo.model.ValutationCriteria;
import demo.utility.PointsLogic;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/playermove")
public class PlayerMoveRest {

	@Autowired
	private PointsLogic pointsLogic;
	
	@Autowired
    private GamesJpa games;
	
	@Autowired
    private UsersJpa users;

	@Autowired
    private PlayerMovesJpa playerMoves;
	
	@Autowired
    private ValutationCriteriaJpa criterias;
	
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
	
	// get all the playerMoves of the logged user
	@RequestMapping(value = "", method = RequestMethod.GET)
	@Transactional
	public List<PlayerMove> getPlayerMoves(Authentication authentication,
			@RequestParam(required=false) Long gameId,
			@RequestParam(required=false) Long criteriaId,
			@RequestParam(defaultValue="false") Boolean gameNotFinished){	
		
		List<PlayerMove> moves;
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User player = users.findOne(currentUser.getUserId());
		if(gameNotFinished)
		{
			if(gameId != null && criteriaId != null)
			{
				Game game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				moves = playerMoves.findByPlayerAndGameAndCriteriaAndGameNotFinished(player, game, criteria);
			}
			else if(gameId != null)
			{
				Game game = games.getOne(gameId);
				moves = playerMoves.findByPlayerAndGameAndGameNotFinished(player, game);
			}
			else if(criteriaId != null)
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
			if(gameId != null && criteriaId != null)
			{
				Game game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				moves = playerMoves.findByPlayerAndGameAndCriteria(player, game, criteria);
			}
			else if(gameId != null)
			{
				Game game = games.getOne(gameId);
				moves = playerMoves.findByPlayerAndGame(player, game);
			}
			else if(criteriaId != null)
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
	
	// get a specific playerMove 
	@RequestMapping("/{playerMoveId}")
	public PlayerMove getPlayerMove(@PathVariable Long playerMoveId){	
		
		PlayerMove playerMove = playerMoves.findOne(playerMoveId);
		
		if(playerMove == null)
		{
			throw new NotFoundException();
		}
		
		return playerMove;
	}
	
	// get all the playersMove of a specific requirementMatrixData 
	@RequestMapping("/requirementsmatrixdata/{requirementsMatrixDataId}")
	public List<PlayerMove> getRequirementsMatrixDataPlayerMove(@PathVariable Long requirementsMatrixDataId){	
		
		RequirementsMatrixData rmd = requirementsMatricesData.findOne(requirementsMatrixDataId);
		
		List<PlayerMove> listPlayerMoves = playerMoves.findByRequirementsMatrixData(rmd);
		
		return listPlayerMoves;
	}
	
	// set the vote for of a player in his/her PlayerMove
	@RequestMapping(method = RequestMethod.PUT, value="/{playerMoveId}/vote/{vote}")
	public Long setPlayerMoveVote(Authentication authentication, @PathVariable Long playerMoveId, @PathVariable Long vote){	
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		PlayerMove playerMove = playerMoves.findOne(playerMoveId);
		playerMove.setValue(vote);
		playerMove.setPlayed(true);
		playerMove.setPlayedTime(new Date());
		playerMoves.save(playerMove);
		
		RequirementsMatrixData rmd = requirementsMatricesData.findOne(playerMove.getRequirementsMatrixData().getRequirementsMatrixDataId());
		
		Long criteriaId = rmd.getCriteria().getCriteriaId();
		
		// add points for player vote
		pointsLogic.addPoint(user, -1l, criteriaId);
				
		return rmd.getGame().getGameId();
	}
	
	// open a playerMove
	@RequestMapping(method = RequestMethod.PUT, value="/open/{playerMoveId}")
	public void openPlayerMove(@PathVariable Long playerMoveId){	
		
		PlayerMove playerMove = playerMoves.findOne(playerMoveId);
		playerMove.setValue(null);
		playerMove.setPlayedTime(null);
		playerMove.setPlayed(false);
		playerMoves.save(playerMove);	
	}
	
	// get all the players of a specific requirmentsMatrixData
	@RequestMapping("/players/{requirementsMatrixDataId}")
	public List<User> getPlayerMovePlayers(@PathVariable Long requirementsMatrixDataId){	
		
		RequirementsMatrixData requirementMatrixData = requirementsMatricesData.getOne(requirementsMatrixDataId);
		
		List<PlayerMove> listPlayerMoves = playerMoves.findByRequirementsMatrixData(requirementMatrixData);
		List<User> movePlayers = new ArrayList<>();
		for(int i=0; i< listPlayerMoves.size();i++){
			movePlayers.add(i,  listPlayerMoves.get(i).getPlayer());
		}
		
		return movePlayers;
	}
}
