package eu.supersede.dm.ga.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ga.GAVirtualDB;
import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.model.Requirement;


@RestController
@RequestMapping("/garp/game")
public class GAGameRest {
	
	@RequestMapping(value="/ownedgames", method = RequestMethod.GET)
	public List<GAGame> getOwnedGames() {
		return GAVirtualDB.get().getOwnedGames( 1L );
	}
//	public List<GAGame> getOwnedGames( Authentication authentication ) {
//		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
//		return GAVirtualDB.get().getOwnedGames( currentUser.getUserId() );
//	}
	
	@RequestMapping(value="/activegames", method = RequestMethod.GET)
	public List<GAGame> getActiveGames() {
		return GAVirtualDB.get().getOwnedGames( 1L );
	}
	
	@RequestMapping(value="/createnew", method = RequestMethod.GET)
	public void createNewGames() {
//		return GAVirtualDB.get().getOwnedGames( 1L );
	}
	
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public void submitPriorities( Authentication authentication, String gameId, List<Requirement> reqs ) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		
		GAVirtualDB.get().setRanking( gameId, currentUser.getUserId(), reqs );
		
	}
	
	@RequestMapping(value="/calc", method = RequestMethod.GET)
	public List<Map<String,Double>> calcRanking( Authentication authentication, GAGame game ) {
		
		IGAAlgorithm algo = new IGAAlgorithm();
		
		algo.setCriteria( game.getCriteria() );
		
		for( String rid : game.getRequirements() ) {
			algo.addRequirement( rid, new ArrayList<>() );
		}
		
		List<Map<String,Double>> prioritization = algo.calc();
		
		return prioritization;
	}
	
}
