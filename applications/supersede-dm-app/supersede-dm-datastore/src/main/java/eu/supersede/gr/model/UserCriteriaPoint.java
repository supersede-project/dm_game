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

package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for UserCriteriaPoint.
*/

@Entity
@Table(name="users_criteria_points")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserCriteriaPoint{
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "criteria_id", nullable = false)
	private ValutationCriteria valutationCriteria;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userCriteriaPointsId;	
    private Long points;
    
    public UserCriteriaPoint(){	
    }
    
    public UserCriteriaPoint(Long points, ValutationCriteria valutationCriteria, User user){
    	this.points = points;
    	this.valutationCriteria = valutationCriteria;
    	this.user = user;
    }
    
    public Long getUserCriteriaPointsId(){
    	return userCriteriaPointsId;
    }
    
    public void setUserCriteriaPointsId(Long userCriteriaPointsId){
    	this.userCriteriaPointsId = userCriteriaPointsId;
    }
    
    public Long getPoints(){
    	return points;
    }
    
    public void setPoints(Long points){
    	this.points = points;
    }
    
    @JsonIgnore
    public User getUser(){
    	return user;
    }
    
    public void setUser(User user){
    	this.user = user;
    }
    
    public ValutationCriteria getValutationCriteria(){
    	return valutationCriteria;
    }
    
    public void setValutationCriteria(ValutationCriteria valutationCriteria){
    	this.valutationCriteria = valutationCriteria;
    }
}