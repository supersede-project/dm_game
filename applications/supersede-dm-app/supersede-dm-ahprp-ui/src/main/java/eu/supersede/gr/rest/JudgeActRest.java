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

import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.AHPGamesJpa;
import eu.supersede.gr.jpa.AHPJudgeActsJpa;
import eu.supersede.gr.jpa.AHPPlayerMovesJpa;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.HAHPJudgeAct;
import eu.supersede.gr.model.HAHPPlayerMove;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.gr.utility.PointsLogic;

@RestController
@RequestMapping("/ahprp/judgeact")
public class JudgeActRest {

	@Autowired
	private PointsLogic pointsLogic;
	
	@Autowired
    private AHPJudgeActsJpa judgeActs;
	
	@Autowired
    private UsersJpa users;

	@Autowired
    private ValutationCriteriaJpa criterias;

	@Autowired
    private AHPGamesJpa games;
	
	@Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesData;
	
	@Autowired
    private AHPPlayerMovesJpa playerMoves;
	
	// get all the judgeActs if user is a judge
//	@PreAuthorize("hasRole('OPINION_NEGOTIATOR')")
	@RequestMapping(value = "",  method = RequestMethod.GET)
	public List<HAHPJudgeAct> getJudgeActs(@RequestParam(required=false) Long gameId,
			@RequestParam(required=false) Long criteriaId,
			@RequestParam(defaultValue="false") Boolean gameNotFinished) {

		List<HAHPJudgeAct> acts;
		if(gameNotFinished)
		{
			if(gameId != null && criteriaId != null)
			{
				HAHPGame game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByGameAndCriteriaAndGameNotFinished(game, criteria);
			}
			else if(gameId != null)
			{
				HAHPGame game = games.getOne(gameId);
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
				HAHPGame game = games.getOne(gameId);
				ValutationCriteria criteria = criterias.getOne(criteriaId);
				acts = judgeActs.findByGameAndCriteria(game, criteria);
			}
			else if(gameId != null)
			{
				HAHPGame game = games.getOne(gameId);
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
//	@PreAuthorize("hasRole('OPINION_NEGOTIATOR')")
	@RequestMapping(value = "/{judgeActId}", method = RequestMethod.GET)
	public HAHPJudgeAct getJudgeAct(@PathVariable Long judgeActId){	
				
		HAHPJudgeAct ja = judgeActs.findOne(judgeActId);
		
		if(ja == null)
		{
			throw new NotFoundException();
		}
		
		return ja;
	}
	
	// set the vote for of a judge in his/her judgeAct
//	@PreAuthorize("hasRole('OPINION_NEGOTIATOR')")
	@RequestMapping(method = RequestMethod.PUT, value="/{judgeActId}/vote/{vote}")
	public void setjudgeActVote(Authentication authentication, @PathVariable Long judgeActId, @PathVariable Long vote){	
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User judge = users.findOne(currentUser.getUserId());
		
		HAHPJudgeAct judgeAct = judgeActs.findOne(judgeActId);
		
		judgeAct.setJudge(judge);
		judgeAct.setVoted(true);
		judgeAct.setVotedTime(new Date());
		judgeActs.save(judgeAct);
		
		HAHPRequirementsMatrixData requirementsMatrixData = judgeAct.getRequirementsMatrixData();		
		requirementsMatrixData.setValue(vote);
		requirementsMatricesData.save(requirementsMatrixData);
		
		Long criteriaId = requirementsMatrixData.getCriteria().getCriteriaId();
		
		// add points for judge move
		pointsLogic.addPoint(judge, -2l, criteriaId);
		
		// set played true to all player_moves connected with the requirementsMatrixDataId		
		List<HAHPPlayerMove> playerMovesList = playerMoves.findByRequirementsMatrixData(requirementsMatrixData);
		for(int i=0; i< playerMovesList.size();i++){
			playerMovesList.get(i).setPlayed(true);
			playerMoves.save(playerMovesList.get(i));
		}
	}
}
