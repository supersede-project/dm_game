package demo.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import demo.jpa.RequirementsMatricesDataJpa;
import demo.jpa.UsersJpa;
import demo.model.CriteriasMatrixData;
import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.PlayerMove;
import demo.model.Requirement;
import demo.model.RequirementsMatrixData;
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
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
	
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
			
			sum = sum + (Math.abs(requirementValueTotal-requirementValuePersonal)*(1.0-requirementValueTotal));
		}
		
		Double agreementIndex = M - (M * sum);
		
		return agreementIndex;
	}
	
	// TODO FINISH!!!!!!!!!! (needed the points in the table)
	// get virtual position (the player of a game with the higher points)
	@RequestMapping(value = "/virtualposition/{gameId}", method = RequestMethod.GET)
	public Double getVirtualPosition(Authentication authentication, @PathVariable Long gameId) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		Game g = games.findOne(gameId);

		
		
		return 1.0;
	}
	
	
	// get position in voting (the player position based on the votes, the player with more votes of a game is the first)
	@RequestMapping(value = "/positioninvoting/{gameId}", method = RequestMethod.GET)
	public Double getPositionInVoting(Authentication authentication, @PathVariable Long gameId) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		Game g = games.findOne(gameId);
		List<User> players = g.getPlayers();
				
		List<RequirementsMatrixData> lrmd = requirementsMatricesData.findByGame(g);
		
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
		
		
		Integer index = indexes.indexOf(user);
		
		return (orderedList.size() - (new Double(index) +1.0)) + 1.0;
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
