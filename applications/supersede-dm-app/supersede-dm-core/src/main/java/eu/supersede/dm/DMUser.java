package eu.supersede.dm;

public class DMUser {
	
	String name;
	DMSkill[] skills;
	
	public DMUser( String name, DMSkill[] skills) {
		this.name = name;
		this.skills = skills;
	}

	public String getName() {
		return this.name;
	}
	
}
