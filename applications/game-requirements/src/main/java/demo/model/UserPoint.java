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