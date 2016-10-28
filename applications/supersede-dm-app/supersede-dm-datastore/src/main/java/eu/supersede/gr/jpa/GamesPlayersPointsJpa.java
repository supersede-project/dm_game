package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.Game;
import eu.supersede.gr.model.GamePlayerPoint;
import eu.supersede.gr.model.User;

public interface GamesPlayersPointsJpa extends JpaRepository<GamePlayerPoint, Long>{

	List<GamePlayerPoint> findByUser(User user);

	GamePlayerPoint findByUserAndGame(User user, Game game);

	List<GamePlayerPoint> findByGame(Game g);

}
