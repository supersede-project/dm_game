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

package demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="users_points")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserPoint{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long usersPointsId;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
    private Long userPoints;
    
    public UserPoint(){
    	this.userPoints = (long) 0;
    }
    
    public Long getUserPoints(){
    	return userPoints;
    }
    
    public void setUserPoints(Long userPoints){
    	this.userPoints = userPoints;
    }
    
    @JsonIgnore
    public User getUser(){
    	return user;
    }
    
    public void setUser(User user){
    	this.user = user;
    }
    
}