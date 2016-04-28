package demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="requirements_choices")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementChoice {

	@Id
    private Long requirementsChoiceId;
    private String description;
    private Long value;
    
    public RequirementChoice(){   	
    }
    
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getRequirementsChoiceId() {
		return requirementsChoiceId;
	}
	public void setRequirementsChoiceId(Long requirementsChoiceId) {
		this.requirementsChoiceId = requirementsChoiceId;
	}   
}
