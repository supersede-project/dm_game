package su.supersede.dm.depcheck.data;

public class XTopic {
	
	public static final XTopic none = new XTopic( "" );
	
	String name = "";
	
	public XTopic() {}
	
	public XTopic( String name ) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return name;
	}
	
}
