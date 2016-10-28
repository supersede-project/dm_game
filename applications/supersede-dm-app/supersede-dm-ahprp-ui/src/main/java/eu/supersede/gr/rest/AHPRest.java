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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.gr.jpa.CriteriasMatricesDataJpa;
import eu.supersede.gr.jpa.GamesJpa;
import eu.supersede.gr.model.CriteriasMatrixData;
import eu.supersede.gr.model.Game;
import eu.supersede.gr.model.PlayerMove;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementsMatrixData;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.dm.algorithms.Ahp;
import eu.supersede.dm.algorithms.AHPStructure;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/ahprp/ahp")
public class AHPRest {

	@Autowired
    private GamesJpa games;
	@Autowired
    private CriteriasMatricesDataJpa criteriasMatricesData;

	// AHP alghoritm for a specific game
	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public Map<String,Double> getValuesFromAlgorithm(@PathVariable Long gameId) {
		
		Game g = games.findOne(gameId);
		
		if(g == null){
			throw new NotFoundException();
		}
		
		List<CriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesData.findByGame(g);
		
		return CalculateAHP(g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());
	}
	
	public static Map<String,Double> CalculateAHP(List<ValutationCriteria> criterias, List<Requirement> requirements, List<CriteriasMatrixData> criteriasMatrixDataList, List<RequirementsMatrixData> requirementsMatrixDataList)
	{
		AHPStructure input = new AHPStructure();
		
		//##################################################################
		// set the criterias of the game
		List<String> criteriasList = new ArrayList<>();
		for(int i=0; i<criterias.size();i++){
			criteriasList.add(criterias.get(i).getCriteriaId().toString());
		}		
		input.setCriteria(criteriasList);
		//##################################################################
		
		//ROW - COL - VALUE
		//##################################################################
		// set the preferences on couple of criteria
		for(int i=0; i<criteriasMatrixDataList.size();i++){
			input.setPreference( criteriasMatrixDataList.get(i).getRowCriteria().getCriteriaId().toString(), 
					criteriasMatrixDataList.get(i).getColumnCriteria().getCriteriaId().toString(), 
					criteriasMatrixDataList.get(i).getValue().intValue());
		}	
		//##################################################################
		
		
		//##################################################################
		// set the requirements of the game
		List<String> requirementsList = new ArrayList<>();
		for(int i=0; i<requirements.size();i++){
			requirementsList.add(requirements.get(i).getRequirementId().toString());
		}		
		input.setOptions(requirementsList);
		//##################################################################

		
		//ROW - COL - CRITERIA - VALUE
		//##################################################################
		// set the preferences between two requirements of a specific criteria
		for(int i=0; i<requirementsMatrixDataList.size();i++){
					
			/*
			//This part allows to find the requirements ranks for a specific player (you have to insert the user_id)
			  
			List<PlayerMove> playerMovesList = requirementsMatrixDataList.get(i).getPlayerMoves();
			
			for(int j=0; j<playerMovesList.size(); j++){
			
				// SET PLAYER ID
				if(playerMovesList.get(j).getPlayer().getUserId() == {{id_user}} && playerMovesList.get(j).getValue() != null){
					input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
							playerMovesList.get(j).getValue().intValue());
				}
			}
			
			
			*/			
			
			if(requirementsMatrixDataList.get(i).getValue() != null && requirementsMatrixDataList.get(i).getValue() != -1L){
				input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
						requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
						requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
						requirementsMatrixDataList.get(i).getValue().intValue());
			}else{
				// find the average
				List<PlayerMove> playerMovesList = requirementsMatrixDataList.get(i).getPlayerMoves();
				
				int total = 0;
				int times = 0;
				int average = 0;
				
				for(int j=0; j<playerMovesList.size(); j++){
					if(playerMovesList.get(j).getValue() != null){
						total = (total + playerMovesList.get(j).getValue().intValue());
						times++;
					}
				}
				
				if(times > 0){
					average = total / times;
					input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
							average);
				}
				
				
			}
			
		}		
		//##################################################################
		Ahp objCalculateRank = new Ahp(input);				
		Map<String,Double> map = objCalculateRank.execute();
		
		return map;
	}
	
	public static Map<String,Double> CalculatePersonalAHP(Long userId, List<ValutationCriteria> criterias, List<Requirement> requirements, List<CriteriasMatrixData> criteriasMatrixDataList, List<RequirementsMatrixData> requirementsMatrixDataList)
	{
		AHPStructure input = new AHPStructure();
		
		//##################################################################
		// set the criterias of the game
		List<String> criteriasList = new ArrayList<>();
		for(int i=0; i<criterias.size();i++){
			criteriasList.add(criterias.get(i).getCriteriaId().toString());
		}		
		input.setCriteria(criteriasList);
		//##################################################################
		
		//ROW - COL - VALUE
		//##################################################################
		// set the preferences on couple of criteria
		for(int i=0; i<criteriasMatrixDataList.size();i++){
			input.setPreference( criteriasMatrixDataList.get(i).getRowCriteria().getCriteriaId().toString(), 
					criteriasMatrixDataList.get(i).getColumnCriteria().getCriteriaId().toString(), 
					criteriasMatrixDataList.get(i).getValue().intValue());
		}	
		//##################################################################
		
		
		//##################################################################
		// set the requirements of the game
		List<String> requirementsList = new ArrayList<>();
		for(int i=0; i<requirements.size();i++){
			requirementsList.add(requirements.get(i).getRequirementId().toString());
		}		
		input.setOptions(requirementsList);
		//##################################################################

		
		//ROW - COL - CRITERIA - VALUE
		//##################################################################
		// set the preferences between two requirements of a specific criteria
		for(int i=0; i<requirementsMatrixDataList.size();i++){
			
			
			
			//This part allows to find the requirements ranks for a specific player (you have to insert the user_id)
			  
			List<PlayerMove> playerMovesList = requirementsMatrixDataList.get(i).getPlayerMoves();
			
			for(int j=0; j<playerMovesList.size(); j++){
			
				// SET PLAYER ID
				if(playerMovesList.get(j).getPlayer().getUserId() == userId && playerMovesList.get(j).getValue() != null){
					input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
							playerMovesList.get(j).getValue().intValue());
				}
			}
			
			
			
			
			/*
			if(requirementsMatrixDataList.get(i).getValue() != null && requirementsMatrixDataList.get(i).getValue() != -1L){
				input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
						requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
						requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
						requirementsMatrixDataList.get(i).getValue().intValue());
			}else{
				// find the average
				List<PlayerMove> playerMovesList = requirementsMatrixDataList.get(i).getPlayerMoves();
				
				int total = 0;
				int times = 0;
				int average = 0;
				
				for(int j=0; j<playerMovesList.size(); j++){
					if(playerMovesList.get(j).getValue() != null){
						total = (total + playerMovesList.get(j).getValue().intValue());
						times++;
					}
				}
				
				if(times > 0){
					average = total / times;
					input.setOptionPreference(requirementsMatrixDataList.get(i).getRowRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getColumnRequirement().getRequirementId().toString(), 
							requirementsMatrixDataList.get(i).getCriteria().getCriteriaId().toString(),
							average);
				}
				
				
			}
			*/
			
		}		
		//##################################################################

		/*
		AHPAnalyser analyser = new AHPAnalyser();	
		Map<String,Double> map = analyser.eval( input );
		*/
		
		Ahp objCalculateRank = new Ahp(input);				
		Map<String,Double> map = objCalculateRank.execute();
		
		return map;
	}
}
