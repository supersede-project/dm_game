package eu.supersede.dm.jmetal;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import eu.supersede.dm.DMActivity;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.DMUser;
import eu.supersede.dm.IDMPlanner;
import eu.supersede.dm.jmetal.permutator.PermutationTable;

public class DMJMetalSingleMethodPlanner implements IDMPlanner {
	
	private DMRuleset ruleset;
	
	public DMJMetalSingleMethodPlanner() {
		this( new DMRuleset() );
	}
	
	public DMJMetalSingleMethodPlanner( DMRuleset ruleset ) {
		this.ruleset = ruleset;
	}
	
	@Override
	public DMSolution solve( DMProblem problem ) {
		
		if( problem.methods().size() > 1 ) {
			throw new RuntimeException( "Unable to solve multi-method problems" );
		}
		
		if( problem.methods().size() < 1 ) {
			throw new RuntimeException( "No candidate method specified" );
		}
		
		PermutationTable<String,String> table = new PermutationTable<>();
		
		{
			List<DMOption> options = problem.methods().get( 0 ).getOptions();
			for( DMOption opt : options ) {
				table.setValues( opt.getName(), opt.getValues() );
			}
		}
		
		for( DMOption option : problem.options() ) {
			table.setValues( option.getName(), option.getValues() );
		}
		
		{
			List<String> list = new ArrayList<>();
			for( DMUser u : problem.users() ) {
				list.add( u.getName() );
			}
			table.setValues( "players", list );
		}
		
		DMActivity activity = findSolution( table, problem );
		
//		Permutation<String,String> p = findSolution( table, problem );
		
		DMSolution solution = new DMSolution();
		
		solution.addActivity( activity );
		
		return solution;
	}
	
	DMActivity findSolution( PermutationTable<String,String> table, DMProblem dmproblem ) {

		CrossoverOperator<IntegerSolution>		crossover					= new IntegerSBXCrossover( 0.1, 0.1 ); // BLXAlphaCrossover( 0.5 );
		MutationOperator<IntegerSolution>		mutation					= new IntegerPolynomialMutation(); // SimpleRandomMutation( 0.5 );
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection	= new RandomSelection<IntegerSolution>();
		SolutionListEvaluator<IntegerSolution>	evaluator					= new SequentialSolutionListEvaluator<IntegerSolution>();
		
		
		PlanningProblem  						problem = new PlanningProblem( table, dmproblem, ruleset );
		
		NSGAII<IntegerSolution> algorithm = new NSGAII<IntegerSolution>( 
				problem, 30, 10, crossover, mutation, selection, evaluator );
		
		
		System.out.print( "Running planner" );
		
		algorithm.run();
		
		System.out.println();
		
		List<IntegerSolution> result = algorithm.getResult();
		
//		for( IntegerSolution s : result ) {
//			System.out.println( s );
//		}
		
		DMActivity activity = new DMActivity( problem.getSelectedMethod().getName(), problem.extract( result.get( 0 ) ) );
		
		return activity;

	}
}
