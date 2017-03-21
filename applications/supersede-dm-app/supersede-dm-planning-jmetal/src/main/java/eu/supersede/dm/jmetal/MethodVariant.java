package eu.supersede.dm.jmetal;

import java.util.HashMap;
import java.util.Map;

public class MethodVariant {
	
	String methodName;
	
	Map<String,String> options = new HashMap<>();

	public MethodVariant( String name ) {
		this.methodName = name;
	}

	public void set( String variable, String value ) {
		this.options.put( variable, value );
	}

	public String asString() {
		String ret = this.methodName;
		ret += "[";
		for( String key : options.keySet() ) {
			ret += key + "=" + options.get(key) + ";";
		}
		ret += "]";
		return ret;
	}
	
}
