package eu.supersede.dm.jmetal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMFitness;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.IDMFitnessCalculator;

public class MultiMethodPlanningProblem extends AbstractIntegerProblem {
	
	private static final long serialVersionUID = 1L;
	
	private DMProblem								problem;
	
//	private PermutationTable<String,String>			table;
	
	DMRuleset										ruleset;
	
//	DMMethod										selectedMethod;
	
	List<MethodVariant> variants;
	List<UserSetting> settings;
	
	public MultiMethodPlanningProblem( List<MethodVariant> variants, List<UserSetting> settings, DMProblem problem, DMRuleset ruleset ) {
		
//		this.table = table;
		
		this.variants = variants;
		this.settings = settings;
		
		this.problem = problem;
		
		this.ruleset = ruleset;
		
		super.setNumberOfObjectives( 1 );
		
		super.setNumberOfVariables( 2 );
		
//		super.setNumberOfVariables( table.getVariableCount() );
		
		{
			Integer[] lowers = new Integer[2];
			Arrays.fill( lowers, 0 );
			super.setLowerLimit( Arrays.asList( lowers ) );
		}
		{
//			int[] b = table.getBoundaries();
			Integer[] uppers = new Integer[2];
			uppers[0] = variants.size() -1;
			uppers[1] = settings.size() -1;
//			for( int i = 0; i < table.getVariableCount(); i++ ) {
//				uppers[i] = b[i] -1;
//			}
			super.setUpperLimit( Arrays.asList( uppers ) );
		}
		
	}
	
	public void evaluate( IntegerSolution solution ) {
		
//		String[] vals = new String[ solution.getNumberOfVariables() ];
		
		MethodVariant mv = variants.get( solution.getVariableValue( 0 ) );
		UserSetting s = settings.get( solution.getVariableValue( 1 ) );
		
//		for( int var = 0; var < solution.getNumberOfVariables(); var++ ) {
//			int i = (int)Double.parseDouble( solution.getVariableValueString( var ) );
//			vals[var] = table.getValue( var, i );
////			System.out.print( table.getVariable( var ) + "='" + table.getValue( var, i ) + "' " );
//		}
		
//		Permutation<String,String> p = Permutation.forTable( table, vals );
		
		DMFitness fitness = new DMFitness();
		
		DMActivityConfiguration activity = new DMActivityConfiguration( mv.methodName, mv.options );
		
//		System.out.print( "Evaluating Activity configuration: " + activity );
		
		System.out.print( "." );
		
		for( IDMFitnessCalculator c : ruleset.rules() ) {
			
			if( c == null ) continue;
			
			DMFitness localFitness = c.evaluate( activity, problem );
			
			System.out.println( "fitness of " + activity.getMethodName() + " = " + localFitness );
			
			fitness.add( localFitness );
			
		}
		
//		System.out.println( " => " + fitness );
		
		solution.setObjective( 0, fitness.value() );
	}

//	public DMMethod getSelectedMethod() {
//		return this.selectedMethod;
//	}

	public Map<String, String> extract( IntegerSolution solution ) {
		
		MethodVariant mv = variants.get( solution.getVariableValue( 0 ) );
		UserSetting s = settings.get( solution.getVariableValue( 1 ) );
		
		Map<String,String> map = new HashMap<>();
		
		map.putAll( mv.options );
		map.putAll( s.map );
		
		return map;
		
//		String[] vals = new String[ solution.getNumberOfVariables() ];
//		for( int var = 0; var < solution.getNumberOfVariables(); var++ ) {
//			int i = (int)Double.parseDouble( solution.getVariableValueString( var ) );
//			vals[var] = table.getValue( var, i );
//		}
//		Permutation<String,String> p = Permutation.forTable( table, vals );
//		return p.asMap();
	}

	public String getMethodName( IntegerSolution solution ) {
		MethodVariant mv = variants.get( solution.getVariableValue( 0 ) );
		return mv.methodName;
	}
}
