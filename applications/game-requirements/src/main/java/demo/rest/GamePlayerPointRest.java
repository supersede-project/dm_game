package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.GamesPlayersPointsJpa;
import demo.model.GamePlayerPoint;

@RestController
@RequestMapping("/gameplayerpoint")
public class GamePlayerPointRest {

	@Autowired
    private GamesPlayersPointsJpa gamesPlayersPoints;
	
	// get all the games players points
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<GamePlayerPoint> getGamesPlayersPoints() {
		return gamesPlayersPoints.findAll();
	}	

}
