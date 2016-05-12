package demo.scheduled;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import demo.jpa.CriteriasMatricesDataJpa;
import demo.jpa.GamesJpa;
import demo.jpa.GamesPlayersPointsJpa;
import demo.jpa.RequirementsMatricesDataJpa;
import demo.model.CriteriasMatrixData;
import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.PlayerMove;
import demo.model.Requirement;
import demo.model.RequirementsMatrixData;
import demo.model.User;
import demo.rest.AHPRest;
import demo.utility.CustomComparator;
import eu.supersede.fe.multitenant.MultiJpaProvider;
 
 @Component
 public class Scheduler {

 	@Autowired
 	MultiJpaProvider multiJpaProvider;
 	
 	private Double M = 20.0;

 	@Scheduled(fixedRate = 1000)
 	private void gameScheduler()
 	{
 		// used for clear the cache
 		multiJpaProvider.clearTenants();
 		
 		Map<String, GamesPlayersPointsJpa> gamesPlayersPointsRepositories = multiJpaProvider.getRepositories(GamesPlayersPointsJpa.class);
 		Map<String, CriteriasMatricesDataJpa> criteriasMatricesDataRepositories = multiJpaProvider.getRepositories(CriteriasMatricesDataJpa.class);
 		Map<String, GamesJpa> gamesRepositories = multiJpaProvider.getRepositories(GamesJpa.class);
 		Map<String, RequirementsMatricesDataJpa> requirementsMatricesDataRepositories = multiJpaProvider.getRepositories(RequirementsMatricesDataJpa.class);
 		
 		for(String tenant : gamesPlayersPointsRepositories.keySet())
 		{
 			GamesPlayersPointsJpa gamesPlayersPointsRepository = gamesPlayersPointsRepositories.get(tenant);
 			CriteriasMatricesDataJpa criteriasMatricesRepository = criteriasMatricesDataRepositories.get(tenant);
 			GamesJpa gamesRepository = gamesRepositories.get(tenant);
 			RequirementsMatricesDataJpa requirementsMatricesRepository = requirementsMatricesDataRepositories.get(tenant);
 			
 			List<GamePlayerPoint> gamesPlayersPoints = gamesPlayersPointsRepository.findAll();
 			
 			// cicle on every gamesPlayersPoints
 			for(int i=0; i < gamesPlayersPoints.size(); i++){
 				
 				Game g = gamesRepository.findOne(gamesPlayersPoints.get(i).getGame().getGameId());
 				
 				// set currentPlayer that is used for other methods
 				g.setCurrentPlayer(gamesPlayersPoints.get(i).getUser());
 				
 	 			List<CriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesRepository.findByGame(g);
 				
 				// #########################################################################
 				//  calculate the agreementIndex for every gamesPlayersPoints of a game and a specific user
 	 			
 	 			Map<String,Double> resultTotal = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());
 	 			Map<String,Double> resultPersonal = AHPRest.CalculatePersonalAHP(gamesPlayersPoints.get(i).getUser().getUserId(),g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());

 	 			List<Requirement> gameRequirements = g.getRequirements();
 	 			
 	 			Double sum = 0.0;
 	 			
 	 			for(int j=0; j < resultTotal.size(); j++){
 	 				Double requirementValueTotal = resultTotal.get(gameRequirements.get(j).getRequirementId().toString());
 	 				Double requirementValuePersonal = resultPersonal.get(gameRequirements.get(j).getRequirementId().toString());
 	 				
 	 				sum = sum + (Math.abs(requirementValueTotal-requirementValuePersonal)*(1.0-requirementValueTotal));
 	 			}
 	 			
 	 			Double agreementIndex = M - (M * sum);
 	 			
 	 			gamesPlayersPoints.get(i).setAgreementIndex(agreementIndex.longValue());
 	 			 	 			
 	 			// #########################################################################
 	 			//  calculate the positionInVoting for every gamesPlayersPoints of a game and a specific user
 	 		
 	 			List<User> players = g.getPlayers();
 	 					
 	 			List<RequirementsMatrixData> lrmd = requirementsMatricesRepository.findByGame(g);
 	 			
 	 			Map<User, Float> gamePlayerVotes = new HashMap<User, Float>();
 	 			
 	 			for (User player : players){
 	 				
 	 				Integer total = 0;
 	 				Integer voted = 0;
 	 				
 	 				if(lrmd != null)
 	 				{
 	 					for(RequirementsMatrixData data : lrmd)
 	 					{
 	 						for(PlayerMove pm : data.getPlayerMoves())
 	 						{
 	 							if(pm.getPlayer().getUserId().equals(player.getUserId()))
 	 							{
 	 								total++;
 	 								if(pm.getPlayed() == true && pm.getValue() != null && !pm.getValue().equals(-1l))
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
 	 					
 	 			List<User> indexes = new ArrayList<User>(orderedList.keySet());		
 	 			Integer index = indexes.indexOf(gamesPlayersPoints.get(i).getUser());
 	 			
 	 			Double positionInVoting = (orderedList.size() - (new Double(index) +1.0)) + 1.0;
 	 			gamesPlayersPoints.get(i).setPositionInVoting(positionInVoting.longValue());
 	 			
 	 			// #########################################################################
 	 			// calculate the virtualPosition of a user base on his/her points in a particular game
 	 			
 	 			GamePlayerPoint gpp = gamesPlayersPointsRepository.findByUserAndGame(gamesPlayersPoints.get(i).getUser(), g);
 	 			List<GamePlayerPoint> specificGamePlayersPoints = gamesPlayersPointsRepository.findByGame(g);
 	 			
 	 			Collections.sort(specificGamePlayersPoints, new CustomComparator());
 	 			
 	 			Long virtualPosition = specificGamePlayersPoints.indexOf(gpp) + 1l;
 	 			gamesPlayersPoints.get(i).setVirtualPosition(virtualPosition);
 	 			
 	 			// #########################################################################
 	 			 	 			
 	 			Long movesPoints = 0l;
 	 			Long gameProgressPoints = 0l;
 	 			Long positionInVotingPoints = 0l;
 	 			Long gameStatusPoints = 0l;
 	 			Long agreementIndexPoints = 0l;
 	 			Long totalPoints = 0l;
 	 			
 	 			// set the movesPoints
 	 			movesPoints = g.getMovesDone().longValue();
 	 			
 	 			//setGameProgressPoints
 	 			gameProgressPoints = (long) Math.floor(g.getPlayerProgress() / 10);
 	 			
 	 			//setPositionInVotingPoints
 	 			if(positionInVoting == 1){
 					positionInVotingPoints = 5l;
 				}else if(positionInVoting == 2){
 					positionInVotingPoints = 3l;
 				}else if(positionInVoting == 3){
 					positionInVotingPoints = 2l;
 				}
 	 			
 	 			//setGameStatusPoints
 	 			if(g.getPlayerProgress() != 100){
 	 				gameStatusPoints =  -20l;
 	 			}else{
 	 				gameStatusPoints = 0l;
 	 			}
 	 			
 	 			//set AgreementIndexPoints
 	 			agreementIndexPoints = agreementIndex.longValue();
 	 			
 	 			totalPoints = movesPoints.longValue() + gameProgressPoints + positionInVotingPoints + gameStatusPoints + agreementIndexPoints;
 	 			
 	 			// set totalPoints 0 if the totalPoints are negative
 	 			if(totalPoints < 0){
 	 				totalPoints = 0l;
 	 			}
 	 			
 	 			gamesPlayersPoints.get(i).setPoints(totalPoints);
 	 			gamesPlayersPointsRepository.save(gamesPlayersPoints.get(i));
 			}	
 		}
 	}
 	
	// method used for sorting a map by values (in this case the number of moves)
	public LinkedHashMap<User, Float> sortHashMapByValues(
	        Map<User, Float> gamePlayerVotes) {
	    List<User> mapKeys = new ArrayList<>(gamePlayerVotes.keySet());
	    List<Float> mapValues = new ArrayList<>(gamePlayerVotes.values());
	    Collections.sort(mapValues);
	    //Collections.sort(mapKeys);

	    LinkedHashMap<User, Float> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Float> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	    	Float val = valueIt.next();
	        Iterator<User> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	        	User key = keyIt.next();
	        	Float comp1 = gamePlayerVotes.get(key);
	        	Float comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}	
 }
 
 
 
 
 
 
 
 