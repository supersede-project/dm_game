package eu.supersede.orch.qpath;

import java.util.ArrayList;
import java.util.List;

public class XPart {
	
	private String nodeName;
	
	List<XCondition> conditions = new ArrayList<>();

	private String fxName = null;
	
	long form, to;
	
	public void setNodeName( String name ) {
		this.nodeName = name;
	}
	
	public String toString() {
		
		String ret = "";
		
		if( nodeName != null ) ret += nodeName;
		
		for( XCondition c : conditions ) {
			ret += " " + c;
		}
		
		if( fxName != null ) ret += "." + fxName;
		
		return ret;
	}
	
	public List<XCondition> getConditions() {
		return this.conditions;
	}
	
	public String getNodeName() {
		return this.nodeName;
	}
	
	public String getFx() {
		return this.fxName;
	}
	
	public long getFrom() {
		return this.form;
	}
	
	public long getTo() {
		return this.to;
	}
	
	public void addCondition( XCondition c ) {
		this.conditions.add( c );
	}

	public void setFunction( String fxName ) {
		this.fxName = fxName;
	}
	
	public void setFrom( long ofs ) {
		this.form = ofs;
	}
	
	public void setTo( long ofs ) {
		this.to = ofs;
	}
	
}
