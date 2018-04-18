package eu.supersede.orch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Measure {
	
	public enum TRIGGER
	{
		BEFORESIMULATIONSTARTS,
		BEFORETASKSTARTS,
		AFTERTASKENDS,
		AFTERSIMULATIONENDS;
		
		public static TRIGGER fromString( String string ) {
			if( string == null ) return BEFORETASKSTARTS;
			string = string.toUpperCase();
			if( BEFORESIMULATIONSTARTS.name().equals( string ) ) return BEFORESIMULATIONSTARTS;
			if( BEFORETASKSTARTS.name().equals( string ) ) return BEFORETASKSTARTS;
			if( AFTERTASKENDS.name().equals( string ) ) return AFTERTASKENDS;
			if( AFTERSIMULATIONENDS.name().equals( string ) ) return AFTERSIMULATIONENDS;
			return BEFORETASKSTARTS;
		}
	}
	
	String					id;
	Map<String,String>		variables = new HashMap<String,String>();
	Map<String,String>		properties = new HashMap<String,String>();
	
	private String			classname;
	private String			code;
	private TRIGGER			trigger;
	
	public Measure( String classname, String id ) {
		this.classname = classname;
		this.id = id;
	}
	
	public Measure( String id ) {
		classname = this.getClass().getSimpleName().toLowerCase();
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String asVar() {
		return "id" + hashCode();
	}
	
	public String toString() {
		return getId();
	}
	
//	public List<Relation> in() {
//		return in;
//	}
//	
//	public List<Relation> out() {
//		return out;
//	}
	
	public String getStereotype() {
		return classname;
	}
	
	public void setVariable( String name, String value ) {
		this.variables.put( name, value );
	}
	
	public Collection<String> variables() {
		return variables.keySet();
	}
	
	public String getVariable( String name, String def ) {
		String ret = variables.get( name );
		if( ret == null ) ret = def;
		return ret;
	}
	
	public String getProperty( String name, String def ) {
		
		String ret = properties.get( name );
		
		if( ret == null ) ret = def;
		
		return ret;
	}
	
//	public List<String> getPropertyArray( String name ) {
//		
//		List<String> list = properties.getList( name );
//		
//		if( list == null ) return new ArrayList<>();
//		
//		return list;
//		
//	}
	
	public double getDouble( String name, double def ) {
		String val = properties.get( name );
		
		if( val == null ) return def;
		
		try {
			return Double.parseDouble( val );
		}
		catch( Exception ex ) {
			return def;
		}
	}
	
	public void setProperty( String name, String value ) {
		if( properties.get( name ) != null ) {
			properties.remove( name );
		}
		properties.put( name, value );
	}
	
	public void setProperty( String name, String[] values ) {
		if( properties.get( name ) != null ) {
			properties.remove( name );
		}
		for( String value : values ) {
			properties.put( name, value );
		}
//		properties.put( name, value );
	}
	
	public Measure withProperty( String name, String value ) {
		setProperty( name, value );
		return this;
	}
	
	public void removeProperty( String name ) {
		properties.remove( name );
	}
	
	public Iterable<String> properties() {
		return properties.keySet();
	}

	public void setCode( String text ) {
		this.code = text;
	}
	
	public String getCode() {
		return this.code;
	}

	public void setTrigger( TRIGGER trigger ) {
		this.trigger = trigger;
	}
	
	public TRIGGER getTrigger() {
		return this.trigger;
	}
	
}
