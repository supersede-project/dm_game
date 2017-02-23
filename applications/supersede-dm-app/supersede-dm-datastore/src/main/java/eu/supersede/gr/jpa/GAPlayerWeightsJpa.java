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

package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HGAPlayerWeight;

public interface GAPlayerWeightsJpa extends JpaRepository<HGAPlayerWeight, Long>
{
    @Query("SELECT weight FROM HGAPlayerWeight playerWeight WHERE gameId = ?1 AND criterionId = ?2 AND userId = ?3")
    Double findPlayerWeightByGameAndCriterion(Long gameId, Long criterionId, Long userId);
}
