package eu.supersede.orch;

public class Objective {
	
	public enum GOAL {
		MAXIMIZE, MINIMIZE, ACHIEVE, PREVENT;
	}
	
	String		id;
	GOAL		goal;
	
	
	public Objective( String id, GOAL goal ) {
		this.id = id;
		this.goal = goal;
	}
	
	public String getId() {
		return this.id;
	}
	
	public GOAL getGoal() {
		return this.goal
		;
	}
	
}
