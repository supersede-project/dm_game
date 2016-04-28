package demo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import demo.model.Game;
import demo.model.User;

public interface GamesJpa extends JpaRepository<Game, Long> {

	List<Game> findByFinished(Boolean finished);
	
	@Query("SELECT game FROM Game game JOIN game.players player WHERE player = ?1")
	List<Game> findByPlayerContains(User player);

	@Query("SELECT game FROM Game game JOIN game.players player WHERE player = ?1 and game.finished = ?2")
	List<Game> findByPlayerContainsAndFinished(User player, Boolean finished);
	
}
