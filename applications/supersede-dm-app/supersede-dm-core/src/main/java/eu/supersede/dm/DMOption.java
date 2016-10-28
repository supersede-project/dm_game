package eu.supersede.dm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DMOption {
	
	String			name;
	
	List<String>	values = new ArrayList<>();
	
	
	public DMOption( String name, List<String> values ) {
		this.name = name;
		this.values = values;
	}

	public DMOption( String name, String[] values ) {
		this.name = name;
		this.values = Arrays.asList( values );
	}


	public String getName() {
		return this.name;
	}


	public List<String> getValues() {
		return this.values;
	}
	
}
