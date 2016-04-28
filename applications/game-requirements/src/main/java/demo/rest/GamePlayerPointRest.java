package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.GamesPlayersPointsJpa;
import demo.jpa.UsersJpa;
import demo.model.GamePlayerPoint;
import demo.model.User;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/gameplayerpoint")
public class GamePlayerPointRest {

	@Autowired
    private GamesPlayersPointsJpa gamesPlayersPoints;
	
	@Autowired
    private UsersJpa users;
	
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

}
