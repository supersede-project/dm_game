package eu.supersede.orch.kb;

import java.util.LinkedList;
import java.util.List;

import eu.supersede.orch.Operation;

public class NodeStatus
{
	
	public enum Presence {
		Present, Absent;
	}
	
	Presence presence = Presence.Present;
	
	LinkedList<Operation> history = new LinkedList<>();
	
	public void setPresence( Presence p ) {
		this.presence = p;
	}

	public void setLastOperation(Operation op) {
		history.addFirst( op );
	}

	public List<Operation> operations() {
		return this.history;
	}
	
}
