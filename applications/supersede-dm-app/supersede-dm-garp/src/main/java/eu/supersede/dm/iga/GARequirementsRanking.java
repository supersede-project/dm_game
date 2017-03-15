package eu.supersede.dm.iga;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author fitsum
 *
 * Class representing a solution. 
 * It contains the list of requirements (ordered according to decreasing priority) and 
 * the corresponding values of the objectives (dissimilarity along the proritization criteria) 
 */

public class GARequirementsRanking {
	private List<String> requirements;
	private Map<String, Double> objectiveValues = new HashMap<String, Double>();
	
	public List<String> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}
	
	public void addObjective (String name, double value){
		objectiveValues.put(name, value);
	}
	
	public Map<String, Double> getObjectiveValues() {
		return objectiveValues;
	}
	public void setObjectiveValues(Map<String, Double> objectiveValues) {
		this.objectiveValues = objectiveValues;
	}
	
}
