package eu.supersede.dm.jmetal;

import java.util.Arrays;
import java.util.Map;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMFitness;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.IDMFitnessCalculator;
import eu.supersede.dm.jmetal.permutator.Permutation;
import eu.supersede.dm.jmetal.permutator.PermutationTable;

public class PlanningProblem extends AbstractIntegerProblem {
	
	private static final long serialVersionUID = 1L;
	
	private DMProblem							problem;

	private PermutationTable<String, String>	table;
	
	DMRuleset									ruleset;
	
	DMMethod									selectedMethod;
	
	public PlanningProblem( PermutationTable<String,String> table, DMProblem problem, DMRuleset ruleset ) {
		
		try {
			this.selectedMethod = problem.getAvailableMethods().get( 0 );
		}
		catch( IndexOutOfBoundsException ex ) {
			throw new RuntimeException( "There must be at least one available method in problem" );
		}
		
		this.table = table;
		
		this.problem = problem;
		
		this.ruleset = ruleset;
		
		super.setNumberOfObjectives( 1 );
		
		super.setNumberOfVariables( table.getVariableCount() );
		
		{
			Integer[] lowers = new Integer[table.getVariableCount()];
			Arrays.fill( lowers, 0 );
			super.setLowerLimit( Arrays.asList( lowers ) );
		}
		{
			int[] b = table.getBoundaries();
			Integer[] uppers = new Integer[table.getVariableCount()];
			for( int i = 0; i < table.getVariableCount(); i++ ) {
				uppers[i] = b[i] -1;
			}
			super.setUpperLimit( Arrays.asList( uppers ) );
		}
		
	}
	
	public void evaluate( IntegerSolution solution ) {
		
		String[] vals = new String[ solution.getNumberOfVariables() ];
		
		for( int var = 0; var < solution.getNumberOfVariables(); var++ ) {
			int i = (int)Double.parseDouble( solution.getVariableValueString( var ) );
			vals[var] = table.getValue( var, i );
//			System.out.print( table.getVariable( var ) + "='" + table.getValue( var, i ) + "' " );
		}
		
		Permutation<String,String> p = Permutation.forTable( table, vals );
		
		DMFitness fitness = new DMFitness();
		
		DMActivityConfiguration activity = new DMActivityConfiguration( this.selectedMethod.getName(), p.asMap() );
		
//		System.out.print( "Evaluating Activity configuration: " + activity );
		
		System.out.print( "." );
		
		for( IDMFitnessCalculator c : ruleset.rules() ) {
			
			if( c == null ) continue;
			
			fitness.add( c.evaluate( activity, problem ) );
			
		}
		
//		System.out.println( " => " + fitness );
		
		solution.setObjective( 0, fitness.value() );
	}

	public DMMethod getSelectedMethod() {
		return this.selectedMethod;
	}

	public Map<String, String> extract( IntegerSolution solution ) {
		String[] vals = new String[ solution.getNumberOfVariables() ];
		for( int var = 0; var < solution.getNumberOfVariables(); var++ ) {
			int i = (int)Double.parseDouble( solution.getVariableValueString( var ) );
			vals[var] = table.getValue( var, i );
		}
		Permutation<String,String> p = Permutation.forTable( table, vals );
		return p.asMap();
	}
}
