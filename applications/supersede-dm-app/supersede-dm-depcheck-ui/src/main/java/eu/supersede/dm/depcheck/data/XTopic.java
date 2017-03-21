package eu.supersede.dm.depcheck.data;

public class XTopic {
	
	String name = "";
	
	public XTopic() {}
	
	public XTopic( String name ) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
}
