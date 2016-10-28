package eu.supersede.dm;

import java.util.HashMap;
import java.util.Map;

public class DMTask {
	
	String name;
	
	Map<String,String> userData = new HashMap<>();
	
	public DMTask(String id) {
		this.name = id;
	}

	public String getId() {
		return this.name;
	}
	
	public void setUserData( String key, String value ) {
		this.userData.put(key, value);
	}
	
	public String getUserData( String key, String def ) {
		String ret = userData.get( key );
		if( ret == null ) {
			ret = def;
		}
		return ret;
	}
	
}
