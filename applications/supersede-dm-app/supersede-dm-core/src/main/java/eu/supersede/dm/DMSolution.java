package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

// TODO: rename DMPlan
public class DMSolution {
	
	List<DMStep>					steps = new ArrayList<>();
	
	public DMSolution() {
	}

	public void addActivity( DMActivityConfiguration activity ) {
		steps.add( new DMStep( activity ) );
	}
	
	public String toString() {
		String ret = steps.size() + " steps: ";
		for( DMStep step : steps ) {
			ret += step.toString();
		}
		return ret;
	}
	
	public List<DMStep> getSteps() {
		return this.steps;
	}
	
}
