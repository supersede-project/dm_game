package eu.supersede.orch;

public class MethodOption {
	
	public enum CARDINALITY {
		SINGLE, MULTI;
	}
	
	public enum DATATYPE {
		NUMBER, STRING, BOOLEAN;
	}
	
	
	private String			name;
	private CARDINALITY		cardinality	= CARDINALITY.SINGLE;
	private DATATYPE		datatype	= DATATYPE.STRING;
	
	
	public MethodOption( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void setCardinality( CARDINALITY cardinality ) {
		this.cardinality = cardinality;
	}
	
	public CARDINALITY getCardinality() {
		return this.cardinality;
	}

	public DATATYPE getDatatype() {
		return datatype;
	}

	public void setDatatype(DATATYPE datatype) {
		this.datatype = datatype;
	}
	
}
