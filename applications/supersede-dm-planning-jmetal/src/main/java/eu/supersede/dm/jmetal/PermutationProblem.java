package eu.supersede.dm.jmetal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import eu.supersede.dm.jmetal.permutator.Permutation;
import eu.supersede.dm.jmetal.permutator.PermutationTable;

public class PermutationProblem extends AbstractIntegerProblem {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, String>					constraints;

	private PermutationTable<String, String>	table;
	
	List<ScoreCalculator>						calculators;
	
	public PermutationProblem( PermutationTable<String,String> table, Map<String, String> constraints, List<ScoreCalculator> calculators ) {
		
		this.table = table;
		
		this.constraints = constraints;
		
		this.calculators = calculators;
		
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
			System.out.print( table.getVariable( var ) + "='" + table.getValue( var, i ) + "' " );
		}
		
		Permutation<String,String> p = Permutation.forTable( table, vals );
		
		double fitness = 0;
		
		for( ScoreCalculator c : calculators ) {
			
			if( c == null ) continue;
			
			fitness += c.calculate( p, constraints );
			
		}
		
		System.out.println( " => " + fitness );
		
		solution.setObjective( 0, fitness );
	}
}
