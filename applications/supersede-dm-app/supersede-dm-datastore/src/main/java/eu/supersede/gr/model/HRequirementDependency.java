package eu.supersede.gr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_requirements_dependencies")
public class HRequirementDependency {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	Long dependerId;
	Long dependeeId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDependerId() {
		return dependerId;
	}
	public void setDependerId(Long dependerId) {
		this.dependerId = dependerId;
	}
	public Long getDependeeId() {
		return dependeeId;
	}
	public void setDependeeId(Long dependeeId) {
		this.dependeeId = dependeeId;
	}
}
