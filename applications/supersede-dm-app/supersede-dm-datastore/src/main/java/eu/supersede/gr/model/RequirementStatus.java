package eu.supersede.gr.model;

import java.util.HashSet;
import java.util.Set;

public enum RequirementStatus {
	
	Unconfirmed (0), Confirmed (1), Enacted (2), Discarded (3);
	
	Integer value;
	
	RequirementStatus( Integer value ) {
		this.value =  value;
	}
	
	public Integer getValue() {
		return this.value;
	}

	public static RequirementStatus valueOf( Integer status ) {
		switch( status ) {
		case 0: return Unconfirmed;
		case 1: return Confirmed;
		case 2: return Enacted;
		case 3: return Discarded;
		default: return Unconfirmed;
		}
	}
	
	public static Set<RequirementStatus> next( RequirementStatus status ) {
		Set<RequirementStatus> set = new HashSet<RequirementStatus>();
		switch( status ) {
		case Unconfirmed:
			set.add( RequirementStatus.Confirmed );
			set.add( RequirementStatus.Discarded );
			break;
		case Confirmed:
			set.add( RequirementStatus.Enacted );
			set.add( RequirementStatus.Discarded );
			break;
		case Discarded:
			set.add( RequirementStatus.Discarded );
			break;
		case Enacted:
			break;
		}
		return set;
	}
	
}
