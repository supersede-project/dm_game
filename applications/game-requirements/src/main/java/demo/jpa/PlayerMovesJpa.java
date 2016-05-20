/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
* @author Andrea Sosi
**/

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

	// Get a List of PlayerMove of a specific player
	List<PlayerMove> findByPlayer(User player);
	
	// Get a List of PlayerMove of a specific player, in a specific Game and on a particular ValutationCriteria
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndCriteria(User player, Game game, ValutationCriteria criteria);
	
	// Get a List of PlayerMove of a specific player and in a specific Game 
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2")
	@Transactional
	List<PlayerMove> findByPlayerAndGame(User player, Game game);
	
	// Get a List of PlayerMove of a specific player and on a particular ValutationCriteria
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2")
	@Transactional
	List<PlayerMove> findByPlayerAndCriteria(User player, ValutationCriteria criteria);

	// Get a List of PlayerMove of a specific player of the Games that are not finished
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameNotFinished(User player);
	
	// Get a List of PlayerMove of a specific player, in a specific Game on a particular ValutationCriteria and the Games are not finished
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndCriteriaAndGameNotFinished(User player, Game game, ValutationCriteria criteria);
	
	// Get a List of PlayerMove of a specific player, in a specific Game and the Games are not finished
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndGameAndGameNotFinished(User player, Game game);
	
	// Get a List of PlayerMove of a specific player on a particular ValutationCriteria and the Games are not finished
	@Query("SELECT pm FROM PlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
	@Transactional
	List<PlayerMove> findByPlayerAndCriteriaAndGameNotFinished(User player, ValutationCriteria criteria);
	
	// Get a List of PlayerMove of a specific RequirementsMatrixData
	List<PlayerMove> findByRequirementsMatrixData(RequirementsMatrixData requirementMatrixData);

}
