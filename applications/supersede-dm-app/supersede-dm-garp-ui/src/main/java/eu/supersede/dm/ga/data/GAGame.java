package eu.supersede.dm.ga.data;

public class GAGame {
	
	Long			id;
	
	Long			owner;
	
	String			date = "";
	
	String			status = "open";
	
	public Long getOwner() {
		return this.owner;
	}

	public Long getId() {
		return this.id;
	}
	
}
