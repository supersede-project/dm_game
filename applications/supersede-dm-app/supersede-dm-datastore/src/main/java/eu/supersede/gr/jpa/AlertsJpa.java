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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import eu.supersede.gr.model.HAlert;

public interface AlertsJpa extends JpaRepository<HAlert, String>
{
    @Query("SELECT alert FROM HAlert alert WHERE applicationId = ?1")
    List<HAlert> findAlertsForApp(String applicationID);

    @Modifying
    @Transactional
    @Query("DELETE FROM HAlert a WHERE id = ?1")
    void deleteById(String id);
}