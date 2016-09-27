package eu.supersede.dm;

public class DMItem {
	
	private String id;
	
	private DMItemType type = DMItemType.Unspecified;
	
	public DMItem( String id, DMItemType type ) {
		this.id = id;
		this.type = type;
	}
	
	public String getId() {
		return this.id;
	}
	
	public DMItemType getType() {
		return this.type;
	}
	
}
