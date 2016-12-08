/**
 * 
 */
package eu.supersede.dm.iga.problem;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import eu.supersede.dm.iga.AhpExperimentMain;
import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.DistanceType;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.WeightType;
import eu.supersede.dm.iga.utils.GAUtils;
import eu.supersede.dm.iga.utils.Utils;

/**
 * @author fitsum
 *
 */
public class ReadAHPTest {

	@Test
	public void testSO() {
		String ahpVotesFile = "resources/input/VOTES/System.csv";
		String dependenciesFile = "resources/input/dependencies";
		String playerWeightsFile = "resources/input/weights_player_presto.csv"; 
		String criteriaWeightsFile = "resources/input/weights_criteria_presto.csv";
		ObjectiveFunction of = ObjectiveFunction.PLAYERS;
		GAVariant gaVariant = GAVariant.SO;
		DistanceType distanceType = DistanceType.KENDALL;
		WeightType weightType = WeightType.EQUAL;
		
	    double crossoverProbability = 0.9 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
		
		
		SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection;
	    
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm;
	    MutationOperator<PermutationSolution<?>> mutation;
	    Problem<PermutationSolution<?>> problem = new SingleObjectivePrioritizationProblem(ahpVotesFile, dependenciesFile, of, gaVariant, distanceType, weightType, playerWeightsFile, criteriaWeightsFile);
		
		double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    mutation = new PermutationSwapMutation (mutationProbability) ;
		
	    selection = new BinaryTournamentSelection<PermutationSolution<?>>(new ObjectiveComparator<PermutationSolution<?>>(0)) ;
		
	    algorithm = new GenerationalGeneticAlgorithm(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
		algorithm.run();
		PrioritizationSolution solution = (PrioritizationSolution)algorithm.getResult();
		System.out.println(solution.toNamedStringWithObjectives());
		GAUtils.printSolutionWithLabels(solution,"solution.csv");
	}

	@Test
	public void testMO() {
		AhpExperimentMain experiment = new AhpExperimentMain();
		String subSystem = "Timeline";
		DistanceType distanceType = DistanceType.KENDALL;
		WeightType weightType = WeightType.EQUAL;
		experiment.runMOGA(distanceType, weightType);
	    
	}
	
	@Test
	public void treeMapTest(){
		SortedMap<String, String[]> criteria = new TreeMap<String, String[]>();
		criteria.put("B", null);
		criteria.put("C", null);
		criteria.put("A", null);
		for (String cr : criteria.keySet()){
			System.out.println(cr);
		}
	}
}
