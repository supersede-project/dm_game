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

import eu.supersede.gr.model.Game;
import eu.supersede.gr.model.User;

public interface GamesJpa extends JpaRepository<Game, Long>
{
    /**
     * Get a List of Game that are finished
     * @param finished
     */
    List<Game> findByFinished(Boolean finished);

    /**
     * Get a List of Game where there is a specific player
     * @param player
     */
    @Query("SELECT game FROM Game game JOIN game.players player WHERE player = ?1")
    List<Game> findByPlayerContains(User player);

    /**
     * Get a List of Game where there is a specific player and that are finished
     * @param player
     * @param finished
     */
    @Query("SELECT game FROM Game game JOIN game.players player WHERE player = ?1 and game.finished = ?2")
    List<Game> findByPlayerContainsAndFinished(User player, Boolean finished);
}