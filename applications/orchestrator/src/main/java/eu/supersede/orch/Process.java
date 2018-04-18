package eu.supersede.orch;

import java.util.ArrayList;
import java.util.List;

public class Process {
	
	List<MethodInstance> instances = new ArrayList<>();
	
	public void append( MethodInstance mi ) {
		instances.add( mi );
	}

	public MethodInstance tail() {
		return instances.get( instances.size() -1 );
	}

	public final List<MethodInstance> workflow() {
		return instances;
	}
	
	public String toString() {
		
		String ret = "Process" + Integer.toHexString(hashCode()) + " (";
		
		for( MethodInstance inst : instances ) {
			
			ret += inst + ";";
			
		}
		
		ret += ")";
		
		return ret;
		
	}

	public void add(MethodInstance mi, int p) {
		instances.add( p, mi );
	}
	
}
