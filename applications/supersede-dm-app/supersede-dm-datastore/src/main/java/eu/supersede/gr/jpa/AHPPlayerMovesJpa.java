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

package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.HAHPPlayerMove;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

public interface AHPPlayerMovesJpa extends JpaRepository<HAHPPlayerMove, Long>
{
    /**
     * Get a List of PlayerMove of a specific player
     * @param player
     */
    List<HAHPPlayerMove> findByPlayer(User player);

    /**
     * Get a List of PlayerMove of a specific player, in a specific Game and on a particular ValutationCriteria
     * @param player
     * @param game
     * @param criteria
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndGameAndCriteria(User player, HAHPGame game, ValutationCriteria criteria);

    /**
     * Get a List of PlayerMove of a specific player and in a specific Game
     * @param player
     * @param game
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndGame(User player, HAHPGame game);

    /**
     * Get a List of PlayerMove of a specific player and on a particular ValutationCriteria
     * @param player
     * @param criteria
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndCriteria(User player, ValutationCriteria criteria);

    /**
     * Get a List of PlayerMove of a specific player of the Games that are not finished
     * @param player
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndGameNotFinished(User player);

    /**
     * Get a List of PlayerMove of a specific player, in a specific Game on a particular ValutationCriteria and the
     * games are not finished
     * @param player
     * @param game
     * @param criteria
     * @return
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.criteria = ?3 AND pm.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndGameAndCriteriaAndGameNotFinished(User player, HAHPGame game,
            ValutationCriteria criteria);

    /**
     * Get a List of PlayerMove of a specific player, in a specific Game and the Games are not finished
     * @param player
     * @param game
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.game = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndGameAndGameNotFinished(User player, HAHPGame game);

    /**
     * Get a List of PlayerMove of a specific player on a particular ValutationCriteria and the Games are not finished
     * @param player
     * @param criteria
     */
    @Query("SELECT pm FROM HAHPPlayerMove pm WHERE pm.player = ?1 AND pm.requirementsMatrixData.criteria = ?2 AND pm.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPPlayerMove> findByPlayerAndCriteriaAndGameNotFinished(User player, ValutationCriteria criteria);

    /**
     * Get a List of PlayerMove of a specific RequirementsMatrixData
     * @param requirementMatrixData
     */
    List<HAHPPlayerMove> findByRequirementsMatrixData(HAHPRequirementsMatrixData requirementMatrixData);
}