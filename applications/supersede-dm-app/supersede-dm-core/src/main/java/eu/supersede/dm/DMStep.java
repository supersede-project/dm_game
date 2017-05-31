package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.Requirement;

public class DMStep {

	List<DMActivityConfiguration>				activities = new ArrayList<>();
	
	public List<Requirement>		requirements = new ArrayList<>();
	
	public DMStep( DMActivityConfiguration activity ) {
		this.activities.add( activity );
	}
	
	public String toString() {
		String ret = "{";
		for( DMActivityConfiguration activity : activities ) {
			ret += "[" + activity + "]";
		}
		ret += "}";
		return ret;
	}

	public List<DMActivityConfiguration> getActivities() {
		return this.activities;
	}
	
}
