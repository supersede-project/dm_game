package eu.supersede.dm.op;

import java.util.HashMap;
import java.util.Map;

import eu.supersede.dm.DMFitness;

public class DMFitnessFunction {
	
	Map<String,DMFitness> map = new HashMap<>();

	public void add( DMFitness f ) {
		map.put( f.getAspect(), f );
	}
	
}
