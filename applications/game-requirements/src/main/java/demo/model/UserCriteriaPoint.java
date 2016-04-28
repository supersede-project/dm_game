package demo.model;

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