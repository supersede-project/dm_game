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
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for Profile.
*/

@Entity
@Table(name="profiles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long profileId;
    private String name;
    @ManyToMany(cascade=CascadeType.ALL, mappedBy="profiles", fetch=FetchType.LAZY)  
    private List<User> users;
    
    public Profile() 
    {
    }
 
    public Profile(String name)
    {
        this.name = name;
    }
 
    public Long getProfileId() 
    {
        return profileId;
    }
 
    public void setProfileId(Long profileId)
    {
        this.profileId = profileId;
    }
 
    public String getName() 
    {
        return name;
    }
 
    public void setName(String name) 
    {
        this.name = name;
    }
    
    @JsonIgnore
    public List<User> getUsers()
    {
    	return users;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
    	if(obj instanceof Profile)
    	{
    		Profile other =(Profile) obj;
    		return other.profileId.equals(profileId);
    	}
    	return false;
    }
}
