package eu.supersede.dm;

import java.util.HashMap;
import java.util.Map;

public class DMLifecycle {
	
	DMPhase initPhase = null;
	
	Map<String,DMPhase> phases = new HashMap<>();
	
	public void addInitialPhase( DMPhase phase ) {
		if( initPhase != null ) {
			throw new RuntimeException( "Initial phase already set" );
		}
		initPhase = phase;
		addPhase( phase );
	}
	
	public void setInitialPhase( String phaseName ) {
		DMPhase phase = phases.get( phaseName );
		if( phase != null ) {
			initPhase = phase;
		}
	}
	
	public void addPhase( DMPhase phase ) {
		phases.put( phase.getName(), phase );
	}
	
	public void setNext( String phaseName, String nextPhaseName ) {
		DMPhase phase = phases.get( phaseName );
		if( phase == null ) {
			return;
		}
		DMPhase next = phases.get( nextPhaseName );
		if( next == null ) {
			return;
		}
		phase.addNext( next );
	}
	
	public DMPhase getInitPhase() {
		return this.initPhase;
	}

	public DMPhase getPhase(String phaseName) {
		return phases.get( phaseName );
	}
	
}
