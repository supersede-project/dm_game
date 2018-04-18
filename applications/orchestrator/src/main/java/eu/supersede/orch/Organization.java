package eu.supersede.orch;

import eu.supersede.orch.kb.DOM;

public class Organization {
	
	private DOM description;

	public Organization( DOM description ) {
		this.description = description;
	}
	
	public DOM getDescription() {
		return this.description;
	}
	
}
