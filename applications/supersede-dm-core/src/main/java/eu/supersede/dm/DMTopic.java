package eu.supersede.dm;

public class DMTopic {
	
	public static final DMTopic none = new DMTopic( "" );
	
	String name = "";
	
	public DMTopic( String name ) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
}
