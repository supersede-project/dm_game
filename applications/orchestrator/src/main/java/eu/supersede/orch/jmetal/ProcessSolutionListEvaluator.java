package eu.supersede.orch.jmetal;

import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class ProcessSolutionListEvaluator implements SolutionListEvaluator<ProcessSolution> {

	private static final long serialVersionUID = 1361901238573570685L;
	
	@Override
	public List<ProcessSolution> evaluate(List<ProcessSolution> solutionList, Problem<ProcessSolution> problem) {
		for (int i = 0 ; i < solutionList.size(); i++) {
			problem.evaluate( solutionList.get(i) );
		}
		return solutionList;
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
