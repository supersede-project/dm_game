package eu.supersede.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DMRuleset {
	
	Map<String,IDMFitnessCalculator> map = new HashMap<>();
	
	List<IDMFitnessCalculator> fitnessCalculators = new ArrayList<>();
	
	public void addRule( String rulename, IDMFitnessCalculator calculator ) {
		this.fitnessCalculators.add( calculator );
		map.put( rulename, calculator );
	}
	
	public List<IDMFitnessCalculator> rules() {
		return this.fitnessCalculators;
	}
	
}
