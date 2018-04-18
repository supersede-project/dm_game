package eu.supersede.orch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Optimality {
	
	Map<String,Double> output = new HashMap<>();
	
	public Collection<String> accumulators() {
		return output.keySet();
	}

	public void add( String id, Double value ) {
		output.put( id,  value );
	}

	public Double getValue(String id) {
		return output.get( id );
	}
	
	public String toString() {
		
		String ret = "";
		
		ret += "{";
		
		for( String key : output.keySet() ) {
			
			ret += key + "=" + output.get( key ) + ";";
			
		}
		
		ret += "}";
		
		return ret;
		
	}
	
}
