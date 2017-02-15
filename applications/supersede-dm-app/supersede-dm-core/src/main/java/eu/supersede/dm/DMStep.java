package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.Requirement;

public class DMStep {

	List<DMActivity>				activities = new ArrayList<>();
	
	public List<Requirement>		requirements = new ArrayList<>();
	
	public DMStep( DMActivity activity ) {
		this.activities.add( activity );
	}
	
	public String toString() {
		String ret = "{";
		for( DMActivity activity : activities ) {
			ret += "[" + activity + "]";
		}
		ret += "}";
		return ret;
	}

	public List<DMActivity> getActivities() {
		return this.activities;
	}
	
}
