package eu.supersede.dm;

public class DMRoleSpec {
	
	DMRole role;
	int min;
	int max;
	
	public DMRoleSpec( DMRole role, int min, int max ) {
		this.role = role;
		this.min = min;
		this.max = max;
	}
	
}
