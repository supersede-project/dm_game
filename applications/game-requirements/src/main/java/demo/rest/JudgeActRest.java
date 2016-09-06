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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.GamesJpa;
import demo.jpa.JudgeActsJpa;
import demo.jpa.PlayerMovesJpa;
import demo.jpa.RequirementsMatricesDataJpa;
import demo.jpa.UsersJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.Game;
import demo.model.JudgeAct;
import demo.model.PlayerMove;
import demo.model.RequirementsMatrixData;
import demo.model.User;
import demo.model.ValutationCriteria;
import demo.utility.PointsLogic;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/judgeact")
public class JudgeActRest {

	@Autowired
	private PointsLogic pointsLogic;
	
	@Autowired
    private JudgeActsJpa judgeActs;
	
	@Autowired
    private UsersJpa users;

	@Autowired
    private ValutationCriteriaJpa criterias;

	@Autowired
    private GamesJpa games;
	
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
	
	@Autowired
    private PlayerMovesJpa playerMoves;
	
	// get all the judgeActs if user is a judge
	@PreAuthorize("hasRole('OPINION_NEGOTIATOR_GAMIFICATION')")
	@RequestMapping(value = "",  method = RequestMethod.GET)
	public List<JudgeAct> getJudgeActs(@RequestParam(required=false) Long gameId,
			@RequestParam(required=false) Long criteriaId,
			@RequestParam(defaultValue="false") Boolean gameNotFinished) {

		List<JudgeAct> acts;
		if(gameNotFinished)
		{
			if(gameId != null && criteriaId != null)
			{
				Game game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByGameAndCriteriaAndGameNotFinished(game, criteria);
			}
			else if(gameId != null)
			{
				Game game = games.getOne(gameId);
				acts = judgeActs.findByGameAndGameNotFinished(game);
			}
			else if(criteriaId != null)
			{
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByCriteriaAndGameNotFinished(criteria);
			}
			else
			{
				acts = judgeActs.findAllAndGameNotFinished();
			}
		}
		else
		{
			if(gameId != null && criteriaId != null)
			{
				Game game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByGameAndCriteria(game, criteria);
			}
			else if(gameId != null)
			{
				Game game = games.getOne(gameId);
				acts = judgeActs.findByGame(game);
			}
			else if(criteriaId != null)
			{
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByCriteria(criteria);
			}
			else
			{
				acts = judgeActs.findAll();
			}
		}
		return acts;
	}
	
	// get a specific judgeAct 
	@PreAuthorize("hasRole('OPINION_NEGOTIATOR_GAMIFICATION')")
	@RequestMapping(value = "/{judgeActId}", method = RequestMethod.GET)
	public JudgeAct getJudgeAct(@PathVariable Long judgeActId){	
				
		JudgeAct ja = judgeActs.findOne(judgeActId);
		
		if(ja == null)
		{
			throw new NotFoundException();
		}
		
		return ja;
	}
	
	// set the vote for of a judge in his/her judgeAct
	@PreAuthorize("hasRole('OPINION_NEGOTIATOR_GAMIFICATION')")
	@RequestMapping(method = RequestMethod.PUT, value="/{judgeActId}/vote/{vote}")
	public void setjudgeActVote(Authentication authentication, @PathVariable Long judgeActId, @PathVariable Long vote){	
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User judge = users.findOne(currentUser.getUserId());
		
		JudgeAct judgeAct = judgeActs.findOne(judgeActId);
		
		judgeAct.setJudge(judge);
		judgeAct.setVoted(true);
		judgeAct.setVotedTime(new Date());
		judgeActs.save(judgeAct);
		
		RequirementsMatrixData requirementsMatrixData = judgeAct.getRequirementsMatrixData();		
		requirementsMatrixData.setValue(vote);
		requirementsMatricesData.save(requirementsMatrixData);
		
		Long criteriaId = requirementsMatrixData.getCriteria().getCriteriaId();
		
		// add points for judge move
		pointsLogic.addPoint(judge, -2l, criteriaId);
		
		// set played true to all player_moves connected with the requirementsMatrixDataId		
		List<PlayerMove> playerMovesList = playerMoves.findByRequirementsMatrixData(requirementsMatrixData);
		for(int i=0; i< playerMovesList.size();i++){
			playerMovesList.get(i).setPlayed(true);
			playerMoves.save(playerMovesList.get(i));
		}
	}
}
