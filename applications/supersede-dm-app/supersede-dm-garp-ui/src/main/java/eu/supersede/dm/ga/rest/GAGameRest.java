package eu.supersede.dm.ga.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ga.GAVirtualDB;
import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.ValutationCriteria;


@RestController
@RequestMapping("/garp/game")
public class GAGameRest {
	
	@Autowired
    private ValutationCriteriaJpa valutationCriterias;
	
	@Autowired
    private RequirementsJpa availableRequirements;
	
	@RequestMapping(value="/ownedgames", method = RequestMethod.GET)
//	public List<GAGame> getOwnedGames() {
//		List<GAGame> list = new ArrayList<>();
//		list.add( new GAGame() );
//		return list;
////		return GAVirtualDB.get().getOwnedGames( 1L );
//	}
	public List<GAGame> getOwnedGames( Authentication authentication ) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		return GAVirtualDB.get().getOwnedGames( currentUser.getUserId() );
	}
	
	@RequestMapping(value="/activegames", method = RequestMethod.GET)
	public List<GAGame> getActiveGames( Authentication authentication ) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		return GAVirtualDB.get().getActiveGames( currentUser.getUserId() );
	}
	
	@RequestMapping(value="/newrandom",method=RequestMethod.GET)
	public GAGame createNewRandomGame( Authentication authentication ) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		Long userId = currentUser.getUserId();
		GAGame game = new GAGame();
		game.setId( System.currentTimeMillis() );
		game.setOwner( userId );
		game.setDate( "12/12/2016" );
		game.setStatus( "open" );
		List<String> gameCriteria = new ArrayList<>();
		{
			List<ValutationCriteria> criteria = valutationCriterias.findAll();
			Collections.shuffle( criteria, new Random(System.nanoTime()));
			for( int i = 0; ((i < 2) | i < criteria.size()); i++ ) {
				gameCriteria.add( criteria.get( i ).getName() );
			}
		}
		List<Long> gameRequirements = new ArrayList<>();
		{
			List<Requirement> requirements = availableRequirements.findAll();
			Collections.shuffle( requirements, new Random(System.nanoTime()));
			int max = new Random(System.currentTimeMillis()).nextInt( requirements.size() );
			for( int i = 0; i < max; i++ ) {
				gameRequirements.add( requirements.get( i ).getRequirementId() );
			}
		}
		GAVirtualDB.get().create( game, gameCriteria, gameRequirements );
		return game;
	}
	
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public void submitPriorities( Authentication authentication, Long gameId, List<String> reqs ) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		GAVirtualDB.get().setRanking( gameId, currentUser.getUserId(), reqs );
	}
	
	@RequestMapping(value="/requirements",method=RequestMethod.POST)
	public List<Requirement> getRequirements( Authentication authentication, Long gameId ) {
		return availableRequirements.findAll();
//		List<Requirement> gameReqs = new ArrayList<>();
//		List<Requirement> reqs = availableRequirements.findAll();
//		for( Long id : GAVirtualDB.get().getRequirements( gameId ) ) {
//			
//		}
//		return gameReqs;
	}
	
	@RequestMapping(value="/calc", method = RequestMethod.GET)
	public List<Map<String,Double>> calcRanking( Authentication authentication, GAGame game ) {
		
		IGAAlgorithm algo = new IGAAlgorithm();
		
		algo.setCriteria( GAVirtualDB.get().getCriteria( game ) );
		
		for( Long rid : GAVirtualDB.get().getRequirements( game.getId() ) ) {
			algo.addRequirement( "" + rid, new ArrayList<>() );
		}
		
		List<Map<String,Double>> prioritization = algo.calc();
		
		return prioritization;
	}
	
}
