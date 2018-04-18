package eu.supersede.orch;

import eu.supersede.orch.kb.AttributeSet;

public class Operation
{
	
	private String path;
	private CRUD type;
	
	private AttributeSet attributes;
	
	private long step;
	
	public String getPath() {
		return this.path;
	}

	public void setPath( String path ) {
		this.path = path;
	}

	public void setType( CRUD type ) {
		this.type = type;
	}
	
	public CRUD getType() {
		return this.type;
	}
	
	public void setAttributes( AttributeSet attr ) {
		this.attributes = attr;
	}

	public AttributeSet getAttribtues() {
		return this.attributes;
	}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}
	
}
