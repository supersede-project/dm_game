package demo.jpa;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import demo.model.Game;
import demo.model.PlayerMove;
import demo.model.RequirementsMatrixData;
import demo.model.User;
import demo.model.ValutationCriteria;

public interface PlayerMovesJpa extends JpaRepository<PlayerMove, Long> {

	List<PlayerMove> findByPlayer(User player);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndCriteria(User player, Game game, ValutationCriteria criteria);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2")
	@Transactional
	List<PlayerMove> findByPlayerAndGame(User player, Game game);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2")
	@Transactional
	List<PlayerMove> findByPlayerAndCriteria(User player, ValutationCriteria criteria);

	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameNotFinished(User player);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndCriteriaAndGameNotFinished(User player, Game game, ValutationCriteria criteria);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndGameNotFinished(User player, Game game);
	
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndCriteriaAndGameNotFinished(User player, ValutationCriteria criteria);
	
	List<PlayerMove> findByRequirementsMatrixData(RequirementsMatrixData requirementMatrixData);

}
