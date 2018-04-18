package eu.supersede.orch;

import eu.supersede.orch.kb.DOM;
import eu.supersede.orch.kb.DomNode;

public class MethodValue {

	private String name;
	private boolean optional;
	private String source;
//	private int min;
//	private int max;
	
	private DOM variables = new DOM();

	public MethodValue( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void setOptional( boolean b ) {
		this.optional = b;
	}
	
	public boolean isOptional() {
		return this.optional;
	}

	public void setSource( String src ) {
		this.source = src;
	}
	
	public String getSource() {
		return this.source;
	}

	public void setCardinality( int min, int max ) {
		set( "min", "" + min );
		set( "max", "" + max );
//		this.min = min;
//		this.max = max;
	}
	
	public int getMin() {
		try {
			return Integer.parseInt( get( "min", "0" ) );
		}
		catch( Exception ex ) {
			return 0;
		}
	}
	
	public int getMax() {
		try {
			return Integer.parseInt( get( "min", "0" ) );
		}
		catch( Exception ex ) {
			return 0;
		}
	}

	public void set( String variable, String value ) {
		variables.create( "/variables" );
		variables.setAttribute( "/variables", variable, value);
	}
	
	public String get( String variable, String def ) {
		DomNode node = variables.getNode( "/variables" );
		return node.getAttribute( variable, def );
	}

}
