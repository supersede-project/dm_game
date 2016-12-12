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
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.DMUser;
import eu.supersede.dm.IDMPlanner;
import eu.supersede.dm.jmetal.permutator.Permutation;
import eu.supersede.dm.jmetal.permutator.PermutationTable;

public class DMJMetalMultiMethodPlanner implements IDMPlanner {

	private DMRuleset ruleset;

	public DMJMetalMultiMethodPlanner() {
		this( new DMRuleset() );
	}

	public DMJMetalMultiMethodPlanner( DMRuleset ruleset ) {
		this.ruleset = ruleset;
	}
	
	List<MethodVariant> extractVariants( DMProblem problem ) {
		
		List<MethodVariant> variants = new ArrayList<>();
		
		for( DMMethod method : problem.methods() ) {
			
			{
				PermutationTable<String,String> table = new PermutationTable<>();
				
				List<DMOption> options = method.getOptions();
				
				for( DMOption opt : options ) {
					table.setValues( opt.getName(), opt.getValues() );
				}
				
				for( Permutation<String,String> p : table ) {
					
					MethodVariant mv = new MethodVariant( method.getName() );
					
					for( int i = 0; i < p.getVariableCount(); i++ ) {
						mv.set( p.variable( i ), p.getValue( p.variable( i ) ) );
					}
					
					variants.add( mv );
					
				}

			}
			
		}


		return variants;
	}
	
	List<String> asStringList( List<MethodVariant> variants ) {
		List<String> list = new ArrayList<>();
		for( MethodVariant mv : variants ) {
			list.add( mv.asString() );
		}
		return list;
	}
	
	@Override
	public DMSolution solve( DMProblem problem ) {
		
		if( problem.methods().size() < 1 ) {
			throw new RuntimeException( "No candidate method specified" );
		}
		
		List<MethodVariant> variants = extractVariants( problem );
		
		List<UserSetting> settings = new ArrayList<>();
		
		{
			
			PermutationTable<String,String> table = new PermutationTable<String,String>();
		
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
			
			for( Permutation<String,String> p : table ) {
				UserSetting setting = new UserSetting();
				for( int i = 0; i < p.getVariableCount(); i++ ) {
					setting.map.put( p.variable(i), p.getValue( p.variable(i) ) );
				}
				settings.add( setting );
			}
			
		}
		
//		table.setValues( "method", asStringList( variants ) );
		
		DMActivity activity = findSolution( variants, settings, problem );
		
		//		Permutation<String,String> p = findSolution( table, problem );
		
		DMSolution solution = new DMSolution();
		
		solution.addActivity( activity );
		
		return solution;
	}
	
	DMActivity findSolution( List<MethodVariant> variants, List<UserSetting> settings, DMProblem dmproblem ) {
		
		CrossoverOperator<IntegerSolution>		crossover					= new IntegerSBXCrossover( 0.1, 0.1 ); // BLXAlphaCrossover( 0.5 );
		MutationOperator<IntegerSolution>		mutation					= new IntegerPolynomialMutation(); // SimpleRandomMutation( 0.5 );
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection	= new RandomSelection<IntegerSolution>();
		SolutionListEvaluator<IntegerSolution>	evaluator					= new SequentialSolutionListEvaluator<IntegerSolution>();
		
		
		MultiMethodPlanningProblem  						problem = new MultiMethodPlanningProblem( variants, settings, dmproblem, ruleset );
		
		NSGAII<IntegerSolution> algorithm = new NSGAII<IntegerSolution>( 
				problem, 30, 10, crossover, mutation, selection, evaluator );
		
		
		System.out.print( "Running planner" );
		
		algorithm.run();
		
		System.out.println();
		
		List<IntegerSolution> result = algorithm.getResult();
		
		DMActivity activity = new DMActivity( problem.getMethodName( result.get( 0 ) ), problem.extract( result.get( 0 ) ) );
		
		return activity;

	}
}
