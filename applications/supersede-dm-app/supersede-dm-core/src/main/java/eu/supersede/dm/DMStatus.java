package eu.supersede.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DMStatus {
	
	Map<String,String> properties = new HashMap<>();
	
	List<DMRequirement> requirements = new ArrayList<>();
	
	public void setProperty( String key, String value ) {
		properties.put( key, value );
	}

	public String getProperty( String key, String def ) {
		String ret = properties.get( key );
		if( ret == null ) {
			ret = def;
		}
		return ret;
	}

	public void addRequirement( DMRequirement r ) {
		requirements.add( r.clone() );
	}

	public List<DMRequirement> requirements() {
		return this.requirements;
	}

}
