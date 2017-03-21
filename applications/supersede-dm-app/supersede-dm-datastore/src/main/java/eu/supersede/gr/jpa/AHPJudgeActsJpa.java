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
import eu.supersede.gr.model.HAHPJudgeAct;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.ValutationCriteria;

public interface AHPJudgeActsJpa extends JpaRepository<HAHPJudgeAct, Long>
{
    /**
     * Get a List of JudgeAct that contain a specified RequirementsMatrixData
     * @param rmd
     */
    List<HAHPJudgeAct> findByRequirementsMatrixData(HAHPRequirementsMatrixData rmd);

    /**
     * Get a List of JudgeAct that contain a specified Game and a specified ValutationCriteria
     * @param game
     * @param criteria
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.game = ?1 AND ja.requirementsMatrixData.criteria = ?2")
    @Transactional
    List<HAHPJudgeAct> findByGameAndCriteria(HAHPGame game, ValutationCriteria criteria);

    /**
     * Get a List of JudgeAct that contain a specified Game
     * @param game
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.game = ?1")
    @Transactional
    List<HAHPJudgeAct> findByGame(HAHPGame game);

    /**
     * Get a List of JudgeAct that contain a specified ValutationCriteria
     * @param criteria
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.criteria = ?1")
    @Transactional
    List<HAHPJudgeAct> findByCriteria(ValutationCriteria criteria);

    /**
     * Get a List of JudgeAct that contain a specified Game, a specified ValutationCriteria and the Game has not to be
     * finished.
     * @param game
     * @param criteria
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.game = ?1 AND ja.requirementsMatrixData.criteria = ?2 AND ja.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPJudgeAct> findByGameAndCriteriaAndGameNotFinished(HAHPGame game, ValutationCriteria criteria);

    /**
     * Get a List of JudgeAct that contain a specified Game but the Game has not to be finished
     * @param game
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.game = ?1 AND ja.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPJudgeAct> findByGameAndGameNotFinished(HAHPGame game);

    /**
     * Get a List of JudgeAct that contain a specified ValutationCriteria but the Game has not to be finished
     * @param criteria
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.criteria = ?1 AND ja.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPJudgeAct> findByCriteriaAndGameNotFinished(ValutationCriteria criteria);

    /**
     * Get a List of JudgeAct where the game are not finished
     */
    @Query("SELECT ja FROM HAHPJudgeAct ja WHERE ja.requirementsMatrixData.game.finished = FALSE")
    @Transactional
    List<HAHPJudgeAct> findAllAndGameNotFinished();
}