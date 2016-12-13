/**
 * 
 */
package eu.supersede.dm.iga.problem;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.PermutationSolution;

import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.DistanceType;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.WeightType;

/**
 * @author fitsum
 *
 */
public class ConstraintViolationTest {

	AbstractPrioritizationProblem problem;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		int numPlayers = 6; // Integer.parseInt(args[1]);
		String inputDirectory = "resources/input/PRESTO/SceneSelection/";
		String criteriaFile = inputDirectory + "criteria.csv"; // args[2];
		String dependenciesFile = inputDirectory + "dependencies"; // args[3];
		String criteriaWeightFile = inputDirectory + "weights_criteria.csv"; // args[4];
		String playerWeightFile = inputDirectory + "weights_player.csv"; // args[5];
		String requirementsFile = inputDirectory + "requirements.csv"; // args[6];
		
		ObjectiveFunction of = ObjectiveFunction.CRITERIA;
		GAVariant gaVariant = GAVariant.SO;
		DistanceType distanceType = DistanceType.KENDALL;
		WeightType weightType = WeightType.EQUAL;
		
		problem = new SingleObjectivePrioritizationProblem(inputDirectory, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType, weightType);
	}

	@Test
	public void testValid() {
		PermutationSolution<?> solution = problem.createSolution();
		problem.evaluate(solution);
		if (problem.violatesDependencyConstraints(solution)){
			System.err.println("violation: " + toString(solution));
		}
	}
	
	private String toString(PermutationSolution<?> solution){
		String s = "";
		for (int i = 0; i < solution.getNumberOfVariables(); i++){
			s += solution.getVariableValueString(i) + ";";
		}
		return s;
	}
	
	@Test
	public void testInvalid(){
		
	}

}
