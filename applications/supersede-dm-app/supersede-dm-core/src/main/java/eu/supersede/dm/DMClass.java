package eu.supersede.dm;

import java.util.HashMap;
import java.util.Map;

public class DMClass {
	
	String						name;
	
	Map<String,DMClassField>	fields = new HashMap<>();
	
	public DMClass( String name ) {
		this.name = name;
	}
	
	public void addField( DMClassField field ) {
		
	}
	
}
