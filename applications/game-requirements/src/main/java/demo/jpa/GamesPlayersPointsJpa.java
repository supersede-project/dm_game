package demo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.User;

public interface GamesPlayersPointsJpa extends JpaRepository<GamePlayerPoint, Long>{

	List<GamePlayerPoint> findByUser(User user);

	GamePlayerPoint findByUserAndGame(User user, Game game);

}
