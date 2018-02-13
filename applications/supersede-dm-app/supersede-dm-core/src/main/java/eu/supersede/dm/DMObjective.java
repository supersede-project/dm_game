package eu.supersede.dm;

public enum DMObjective {
	
	PrioritizeRequirements, 
	TagItemsAsRequirements, 
	EstablishRequirementDependencies, 
	AssignRequirementTopics;
	
	//changed in order to get 
    public String toString() {
        return name().replace("Requirement", "Feature");
    }
	
}
