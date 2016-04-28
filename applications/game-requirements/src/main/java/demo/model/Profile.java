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
