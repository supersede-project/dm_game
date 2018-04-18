package eu.supersede.orch.jmetal;

import java.util.List;

import org.uma.jmetal.operator.SelectionOperator;

public class ProcessSelectionOperator implements SelectionOperator<List<ProcessSolution>, ProcessSolution>  {

	@Override
	public ProcessSolution execute(List<ProcessSolution> source) {
		if( source.size() > 0 ) {
			return source.get( 0 ).copy();
//			return source.get( RandomProvider.get().getRandom().nextInt( source.size() ) ).copy();
		}
		return null;
	}

}
