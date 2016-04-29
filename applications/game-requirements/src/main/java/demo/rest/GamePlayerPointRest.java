package demo.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.CriteriasMatricesDataJpa;
import demo.jpa.GamesJpa;
import demo.jpa.GamesPlayersPointsJpa;
import demo.jpa.UsersJpa;
import demo.model.CriteriasMatrixData;
import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.Requirement;
import demo.model.User;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/gameplayerpoint")
public class GamePlayerPointRest {

	private Double M = 20.0;
	
	@Autowired
    private GamesPlayersPointsJpa gamesPlayersPoints;
	@Autowired
    private UsersJpa users;	
	@Autowired
    private GamesJpa games;
	@Autowired
    private CriteriasMatricesDataJpa criteriasMatricesData;
	
	// get all the gamesPlayersPoints
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<GamePlayerPoint> getGamesPlayersPoints() {
		return gamesPlayersPoints.findAll();
	}
	
	// get all the gamesPlayersPoints for logged user
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public List<GamePlayerPoint> getUserGamesPlayersPoints(Authentication authentication) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		return gamesPlayersPoints.findByUser(user);
	}
	
	// get the gamesPlayersPoints of a specific user and game
	@RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
	public GamePlayerPoint getSpecificGameGamesPlayersPoints(Authentication authentication, @PathVariable Long gameId) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		Game game = games.findOne(gameId);
		
		return gamesPlayersPoints.findByUserAndGame(user, game);
	}
	
	// get agreement index
	@RequestMapping(value = "/agreementindex/{gameId}", method = RequestMethod.GET)
	public Double getAgreementIndex(Authentication authentication, @PathVariable Long gameId) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		Game g = games.findOne(gameId);
		List<CriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesData.findByGame(g);
		
		Map<String,Double> resultTotal = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());
		Map<String,Double> resultPersonal = AHPRest.CalculatePersonalAHP(user.getUserId(),g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());

		List<Requirement> gameRequirements = g.getRequirements();
		
		Double sum = 0.0;
		
		for(int i=0; i < resultTotal.size(); i++){
			Double requirementValueTotal = resultTotal.get(gameRequirements.get(i).getRequirementId().toString());
			Double requirementValuePersonal = resultPersonal.get(gameRequirements.get(i).getRequirementId().toString());
			System.out.println("TOTAL: "+ requirementValueTotal);
			System.out.println("PERSONAL: "+ requirementValuePersonal);

			
			sum = sum + (Math.abs(requirementValueTotal-requirementValuePersonal)*(1.0-requirementValueTotal));
		}
		
		Double agreementIndex = M - (M * sum);
		
		return agreementIndex;
	}
}
