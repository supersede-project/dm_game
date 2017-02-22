package eu.supersede.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.gr.model.HRequirementDependency;
import eu.supersede.gr.model.HRequirementProperty;
import eu.supersede.gr.model.Requirement;

public class RequirementDetails {
	
	Requirement							requirement;
	List<HRequirementDependency>		dependencies = new ArrayList<>();
	Map<String,HRequirementProperty>	properties = new HashMap<>();
	
	public RequirementDetails() {}
	
	public RequirementDetails( Requirement r ) {
		this.requirement = r;
	}
	
	public Requirement getRequirement() {
		return requirement;
	}

	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}

	public List<HRequirementDependency> getDependencies() {
		return dependencies;
	}

	public void addDependency( HRequirementDependency dependency ) {
		this.dependencies.add( dependency );
	}

	public Collection<HRequirementProperty> getProperties() {
		return properties.values();
	}

	public void setProperty( HRequirementProperty property ) {
		this.properties.put( property.getName(), property );
	}

}
