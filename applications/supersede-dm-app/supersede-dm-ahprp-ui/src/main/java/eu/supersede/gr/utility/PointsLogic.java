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

package eu.supersede.gr.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.gr.jpa.PointsJpa;
import eu.supersede.gr.jpa.UserCriteriaPointsJpa;
import eu.supersede.gr.jpa.UserPointsJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Point;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.UserCriteriaPoint;
import eu.supersede.gr.model.UserPoint;
import eu.supersede.gr.model.ValutationCriteria;

@Component
public class PointsLogic {
	
	@Autowired
    private UserPointsJpa userPoints;
	
	@Autowired
    private PointsJpa points;
	
	@Autowired
    private ValutationCriteriaJpa criterias;
	
	@Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;
	
	public void addPoint(User user,  Long pointId,  Long criteriaId){	
		
		Point point = points.findOne(pointId);
				
		if(criteriaId != -1){
		
			ValutationCriteria criteria = criterias.findOne(criteriaId);
			UserCriteriaPoint ucp = userCriteriaPoints.findByValutationCriteriaAndUser(criteria, user);
			
			if(ucp == null){
				UserCriteriaPoint newUcp = new UserCriteriaPoint();
				
				newUcp.setUser(user);
				newUcp.setValutationCriteria(criteria);
				newUcp.setPoints(point.getCriteriaPoints());
				userCriteriaPoints.save(newUcp);
			}else{
				ucp.setPoints(ucp.getPoints() + point.getCriteriaPoints());
				userCriteriaPoints.save(ucp);
			}
		}
		
		UserPoint up = userPoints.findByUser(user);
		
		if(up == null){
			UserPoint newUp = new UserPoint();
			
			newUp.setUser(user);
			newUp.setUserPoints(point.getGlobalPoints());
			userPoints.save(newUp);
		}else{
			up.setUserPoints(up.getUserPoints() + point.getGlobalPoints());
			userPoints.save(up);
		}
	}
}