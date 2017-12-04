package eu.supersede.dm.op;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity(difficultyComparatorClass = ProcessDifficultyComparator.class)
public class ActivityExecution implements ProcessActivity {
	
	String								name;
	
	// planning variable
	ProcessActivity							prev = null; //new UserAssignment(); //ProcessEnd.instance;
	
	// shadow variable
	private ProcessActivity					next;
	
	
	public ActivityExecution() {
		
	}
	
	public ActivityExecution( String name ) {
		this.name = name;
	}
    
	@PlanningVariable( 
			graphType = PlanningVariableGraphType.CHAINED, 
			valueRangeProviderRefs = {"executionPool", "userPool"} )
	public ProcessActivity getPrev() {
		return prev;
	}
	
	public void setPrev( ProcessActivity a ) {
		if( this.prev != null ) {
			this.prev.setNext( null );
		}
		this.prev = a;
//		System.out.println( "Setting " + a.getClass().getSimpleName() + ".next = " + getName() );
		if( a != null ) {
			a.setNext( this );
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public void setNext(ProcessActivity a) {
		this.next = a;
	}

	@Override
	public ProcessActivity getNext() {
		return this.next;
	}
    
	public String toString() {
		return name + " > " + (next != null ? next : "END");
	}
}
