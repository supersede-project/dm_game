package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.GamePlayerPoint;

public interface GamesPlayersPointsJpa extends JpaRepository<GamePlayerPoint, Long>{

}
