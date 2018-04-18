package eu.supersede.orch.qpath;

public class XCondition {
	
	String target;
	String comparator;
	String value;
	
	public String getTarget() {
		return this.target;
	}
	
	public String getComparator() {
		return this.comparator;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setTarget( String target ) {
		this.target = target;
	}

	public void setComparator( String comp ) {
		this.comparator = comp;
	}

	public void setValue( String value ) {
		if( value == null ) value = "";
		if( value.startsWith("'") ) value = value.substring( 1 );
		if( value.endsWith( "'" ) )  value = value.substring( 0, value.length() -1 );
		this.value = value;
	}
	
	public String toString() {
		return "[" + "" + target + " " + comparator + " " + value + "]";
	}

}
