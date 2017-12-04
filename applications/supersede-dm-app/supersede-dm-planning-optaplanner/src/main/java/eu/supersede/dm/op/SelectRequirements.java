package eu.supersede.dm.op;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

public class SelectRequirements extends ActivityExecution {
	
	List<String> requirements = new ArrayList<>();
	
	@PlanningVariable( 
			graphType = PlanningVariableGraphType.CHAINED, 
			valueRangeProviderRefs = {"requirementsPool"} )
	public ProcessActivity getPrev() {
		return prev;
	}
	
}
