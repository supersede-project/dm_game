package eu.supersede.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DMActivityConfiguration {
	
	String				methodName;
	
	List<DMParticipant>	players = new ArrayList<>();
	
	Map<String,String>	options;
	
	public DMActivityConfiguration( String methodName, Map<String,String> options ) {
		this.methodName = methodName;
		this.options = options;
	}
	
	public String getOption( String name ) {
		return options.get( name );
	}
	
	public Collection<String> getOptions() {
		return options.keySet();
	}
	
	public String toString() {
		return methodName + "; options:" + options + "; players:" + players;
	}

	public String getMethodName() {
		return this.methodName;
	}
	

}
