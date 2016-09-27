package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

public class DMRuleset {
	
	List<IDMFitnessCalculator> fitnessCalculators = new ArrayList<>();
	
	public void addRule( IDMFitnessCalculator calculator ) {
		this.fitnessCalculators.add( calculator );
	}
	
	public List<IDMFitnessCalculator> rules() {
		return this.fitnessCalculators;
	}
	
}
