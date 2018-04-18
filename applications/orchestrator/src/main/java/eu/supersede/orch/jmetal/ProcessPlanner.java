package eu.supersede.orch.jmetal;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import eu.supersede.orch.Evaluation;
import eu.supersede.orch.OptimalityModel;
import eu.supersede.orch.Objective;
import eu.supersede.orch.ProcessSpace;
import eu.supersede.orch.Simulator;
import eu.supersede.orch.kb.KnowledgeBase;

public class ProcessPlanner {
	
	public List<ProcessSolution> plan( ProcessSpace ps, KnowledgeBase kb, OptimalityModel model, List<Objective> objectives, Simulator simulator ) {

		CrossoverOperator<ProcessSolution> crossover = 
				new ProcessCrossoverOperator();
		
		MutationOperator<ProcessSolution> mutator = 
				new ProcessMutationOperator( ps );
		
		SelectionOperator<List<ProcessSolution>, ProcessSolution> selector = 
				new ProcessSelectionOperator();
		
		SolutionListEvaluator<ProcessSolution> evaluator = 
				new ProcessSolutionListEvaluator();
		
		
		ProcessPlanningProblem problem = new ProcessPlanningProblem( ps, kb, model, objectives, simulator );
		
		NSGAII<ProcessSolution> algorithm = new NSGAII<ProcessSolution>( 
				problem, 30, 10, crossover, mutator, selector, evaluator );
		
		
		System.out.println( "Running planner" );
		
		algorithm.run();
		
		System.out.println();
		
		List<ProcessSolution> result = algorithm.getResult();
		
		for( ProcessSolution sol : result ) {
			
			Evaluation eval = sol.getEvaluation();
			
			System.out.println( eval );
			
			System.out.println( sol );
			
		}
		
		return result;
		
	}
	
}
