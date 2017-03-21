package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.HUserSkill;

public class DMUser {
	
	String		name;
	
	List<HUserSkill> skills;
	
//	DMSkill[] skills;
	
	public DMUser( String name ) {
		this( name, new ArrayList<>() );
	}

	public DMUser( String name, List<HUserSkill> skills) {
		this.name = name;
		this.skills = skills;
	}

	public DMUser( String name, HUserSkill[] skills) {
		this.name = name;
		this.skills = new ArrayList<>();
		for( HUserSkill s : skills ) {
			this.skills.add( s );
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		String string = name;
		string += " [";
		for( HUserSkill skill : skills ) {
			string += skill + ";";
		}
		string += "]";
		return string;
	}
	
}
