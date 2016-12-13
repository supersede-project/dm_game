package eu.supersede.dm.ga.data;

import java.util.ArrayList;
import java.util.List;

public class GAGame {
	
	Long			owner;
	
	List<Long>		participants = new ArrayList<>();
	
	List<String>	criteria = new ArrayList<>();
	
	List<String>	requirements = new ArrayList<>();
	
	public Long getOwner() {
		return this.owner;
	}

	public List<Long> getParticipants() {
		return this.participants;
	}

	public List<String> getRequirements() {
		return this.requirements;
	}
	
	public List<String> getCriteria() {
		return this.criteria;
	}
	
}
