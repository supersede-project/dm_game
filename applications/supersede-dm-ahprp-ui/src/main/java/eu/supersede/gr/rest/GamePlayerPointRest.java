package eu.supersede.gr.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.gr.jpa.GamesJpa;
import eu.supersede.gr.jpa.GamesPlayersPointsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.model.Game;
import eu.supersede.gr.model.GamePlayerPoint;
import eu.supersede.gr.model.User;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/ahprp/gameplayerpoint")
public class GamePlayerPointRest {
	
	@Autowired
    private GamesPlayersPointsJpa gamesPlayersPoints;
	@Autowired
    private UsersJpa users;	
	@Autowired
    private GamesJpa games;
	
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
}
