package demo.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="valutation_criterias")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ValutationCriteria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long criteriaId;
    private String name;
    private String description;
    
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "valutationCriteria")
	private List<UserCriteriaPoint> userCriteriaPoints;

    public ValutationCriteria() {
    }
 
    public ValutationCriteria(String name) {
        this.name = name;
    }
 
    public Long getCriteriaId() {
        return criteriaId;
    }
 
    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
    @JsonIgnore
    public List<UserCriteriaPoint> getUserCriteriaPoints(){
    	return userCriteriaPoints;
    }
    
    public void setUserCriteriaPoints(List<UserCriteriaPoint> userCriteriaPoints){
    	this.userCriteriaPoints = userCriteriaPoints;
    }
}
