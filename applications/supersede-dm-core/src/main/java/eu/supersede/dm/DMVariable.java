package eu.supersede.dm;

import java.util.HashMap;
import java.util.Map;

public class DMVariable {
	
	Map<String,String>		attributes = new HashMap<>();
	
	Map<String,DMVariable>	children = new HashMap<>();
	
	DMValue					value;
	
}
