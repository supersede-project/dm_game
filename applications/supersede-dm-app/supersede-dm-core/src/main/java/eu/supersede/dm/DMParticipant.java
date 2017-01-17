package eu.supersede.dm;

public class DMParticipant {
	
	DMUser user;
	DMRole role;
	
	public String getName() {
		return user.name;
	}
	
	public String toString() {
		return user + "(" + role + ")";
	}
	
}
