package eu.supersede.orch;

import eu.supersede.orch.kb.DOM;
import eu.supersede.orch.kb.DomNode;

public class MethodInstance {
	
	MethodDefinition md;
	
	DOM configuration = new DOM();
	
	DOM inputs = new DOM();
	
	
	public MethodInstance() {
		configuration.create( "/options" );
	}
	
	public MethodDefinition getDefinition() {
		return this.md;
	}
	
	public String toString() {
		String ret = md.getName();
		ret += "(";
		for( DomNode node : configuration.getNode( "/options" ).children() ) {
			ret += node.getName() + ":" + node.getAttribute( "value", "" ) + ";";
		}
		ret += ")";
		return ret;
	}

	public MethodInstance copy() {
		MethodInstance copy = new MethodInstance();
		copy.md = this.md;
		copy.configuration = this.configuration.clone();
		return copy;
	}
	
	public void setOption( String key, String value ) {
		configuration.create( "/options/" + key );
		configuration.setAttribute( "/options/" + key, "value", value );
	}
	
	public DOM getConfiguration() {
		return this.configuration;
	}
	
	public DOM getInputs() {
		return this.inputs;
	}
	
}
