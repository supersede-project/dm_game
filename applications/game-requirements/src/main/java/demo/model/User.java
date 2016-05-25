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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
	Model class for User.
*/

@Entity
@Table(name="users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String name;
    private String email;
    private String password;
    @ManyToMany(cascade=CascadeType.ALL)  
    @JoinTable(name="users_profiles", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="profile_id"))  
    private List<Profile> profiles;
    
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserCriteriaPoint> userCriteriaPoints;
	
	@OneToOne(mappedBy = "user", optional = true)
	private UserPoint userGlobalPoints;
	
    public User() {
    }
 
    public User(String name, String email, String password, List<Profile> profiles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profiles = profiles;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    @JsonIgnore
    public String getPassword() {
        return password;
    }
 
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Long getPoints() {
    	long tmpPoints = 0;
    	
    	for(int i=0;i<userCriteriaPoints.size();i++){
    		tmpPoints += userCriteriaPoints.get(i).getPoints();
    	}
    	if(userGlobalPoints != null)
    	{
    		tmpPoints+= userGlobalPoints.getUserPoints();
    	}
    	return tmpPoints;
    }
 
    public List<Profile> getProfiles() {
        return profiles;
    }
 
    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
    
    public List<UserCriteriaPoint> getUserCriteriaPoints(){
    	return userCriteriaPoints;
    }
    
    public void setUserCriteriaPoints(List<UserCriteriaPoint> userCriteriaPoints){
    	this.userCriteriaPoints = userCriteriaPoints;
    } 
    
    public UserPoint getUserGlobalPoints(){
    	return userGlobalPoints;
    }
    
    public void setUserGlobalPoints(UserPoint userGlobalPoints){
    	this.userGlobalPoints = userGlobalPoints;
    }
    
}
