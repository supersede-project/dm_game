package demo.scheduled;
 
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import demo.jpa.CriteriasMatricesDataJpa;
import demo.jpa.GamesJpa;
import demo.jpa.GamesPlayersPointsJpa;
import demo.model.CriteriasMatrixData;
import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.Requirement;
import demo.rest.AHPRest;
import eu.supersede.fe.multitenant.MultiJpaProvider;
 
 @Component
 public class Scheduler {

 	@Autowired
 	MultiJpaProvider multiJpaProvider;
 	
 	private Double M = 20.0;

 	@Scheduled(fixedRate = 10000)
 	private void notifyJudges()
 	{
 		Map<String, GamesPlayersPointsJpa> gamesPlayersPointsRepositories = multiJpaProvider.getRepositories(GamesPlayersPointsJpa.class);
 		Map<String, CriteriasMatricesDataJpa> criteriasMatricesDataRepositories = multiJpaProvider.getRepositories(CriteriasMatricesDataJpa.class);
 		Map<String, GamesJpa> gamesRepositories = multiJpaProvider.getRepositories(GamesJpa.class);
 		
 		for(String tenant : gamesPlayersPointsRepositories.keySet())
 		{
 			GamesPlayersPointsJpa gamesPlayersPointsRepository = gamesPlayersPointsRepositories.get(tenant);
 			CriteriasMatricesDataJpa criteriasMatricesRepository = criteriasMatricesDataRepositories.get(tenant);
 			GamesJpa gamesRepository = gamesRepositories.get(tenant);
 			
 			List<GamePlayerPoint> gamesPlayersPoints = gamesPlayersPointsRepository.findAll();
 			
 			// calculate the agreementIndex for every gamesPlayersPoints of a game and a specific user
 			for(int i=0; i < gamesPlayersPoints.size(); i++){
 				
 				Game g = gamesRepository.findOne(gamesPlayersPoints.get(i).getGame().getGameId());
 	 			List<CriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesRepository.findByGame(g);
 	 			
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
 	 			
 	 			//save 
 	 			gamesPlayersPoints.get(i).setAgreementIndex(agreementIndex.longValue());
 	 			gamesPlayersPointsRepository.save(gamesPlayersPoints.get(i));
 	 			
 	 			System.out.println(tenant +"GAME: " + gamesPlayersPoints.get(i).getGame().getTitle() + " , INDEX: " + agreementIndex + " , PLAYER: " + gamesPlayersPoints.get(i).getUser().getName());
 			}	
 		}

 	}
 }
 
 
 
 
 
 
 
 