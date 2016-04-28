package demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="points")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Point {

	@Id
    private Long pointId;	
	private String description;
	private Long globalPoints;
	private Long criteriaPoints;
	
	public Point(){
	}
	
	public Long getPointId() {
		return pointId;
	}

	public void setPointId(Long pointId) {
		this.pointId = pointId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getGlobalPoints() {
		return globalPoints;
	}

	public void setGlobalPoints(Long globalPoints) {
		this.globalPoints = globalPoints;
	}

	public Long getCriteriaPoints() {
		return criteriaPoints;
	}

	public void setCriteriaPoints(Long criteriaPoints) {
		this.criteriaPoints = criteriaPoints;
	}	
}
