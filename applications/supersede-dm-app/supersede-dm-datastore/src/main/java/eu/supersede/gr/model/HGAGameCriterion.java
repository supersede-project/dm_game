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

package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_ga_game_riteria")
public class HGAGameCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long		id;
    
	Long		gameId;
	
	Long		criterionId;
	
	Boolean		inverse = false;
	
	HGAGameCriterion() {}
	
	public HGAGameCriterion( Long gameid, Long criterionid ) {
		this.gameId = gameid;
		this.criterionId = criterionid;
	}

}
