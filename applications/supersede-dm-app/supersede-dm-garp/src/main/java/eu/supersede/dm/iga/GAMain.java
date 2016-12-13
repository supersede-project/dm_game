/**
 * 
 */
package eu.supersede.dm.iga;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.SteadyStateGeneticAlgorithm;
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

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.WeightType;
import eu.supersede.dm.iga.problem.MultiObjectivePrioritizationProblem;
import eu.supersede.dm.iga.problem.SingleObjectivePrioritizationProblem;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.DistanceType;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.utils.GAUtils;

/**
 * @author fitsum
 *
 */
public class GAMain {
	/**
	 * Map integer values used in the GA solution representation to
	 * requirement IDs (such as R1, R2, ...) for human consumption 
	 */
	public static Map<Integer, String> requirementIdMap = new HashMap<Integer, String>();
	
	/**
	 * 
	 */
	public GAMain() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		GAVariant gaVariant = GAVariant.MO; // multi-objective
		if (args.length < 2){
			System.err.println("Please provide the path to input directory, output directory, and an optional parameter to indicate type of algorithm: SO or MO, defaults to MO");
			System.exit(0);
		}
		
		String inputDirectory = args[0];
		String outputDirectory = args[1];
		if (args.length == 3){
			gaVariant = GAVariant.valueOf(args[2]);
		}
		
		// set common parameters for the algorithms
//		int numPlayers = 6; // Integer.parseInt(args[1]);
		String criteriaFile = inputDirectory + "/criteria.csv"; // args[2];
		String dependenciesFile = inputDirectory + "/dependencies"; // args[3];
		String criteriaWeightFile = inputDirectory + "/weights_criteria.csv"; // args[4];
		String playerWeightFile = inputDirectory + "/weights_player.csv"; // args[5];
		String requirementsFile = inputDirectory + "/requirements.csv"; // args[6];
		
		ObjectiveFunction of = ObjectiveFunction.CRITERIA;
		DistanceType distanceType = DistanceType.KENDALL;
		WeightType weightType = WeightType.INPUT;
				
	    double crossoverProbability = 0.9 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
	    
	    SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection;
	    
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm;
	    MutationOperator<PermutationSolution<?>> mutation;
	    Problem<PermutationSolution<?>> problem; 
	    
		// if single-objective
		if (gaVariant == GAVariant.SO){
			problem = new SingleObjectivePrioritizationProblem(inputDirectory, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType, weightType);
			
			double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
		    mutation = new PermutationSwapMutation (mutationProbability) ;
			
		    selection = new BinaryTournamentSelection<PermutationSolution<?>>(new ObjectiveComparator<PermutationSolution<?>>(0)) ;
			
		    algorithm = new GenerationalGeneticAlgorithm(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
			algorithm.run();
			PrioritizationSolution solution = (PrioritizationSolution)algorithm.getResult();
			System.out.println(solution.toNamedStringWithObjectives());
			GAUtils.printSolutionWithLabels(solution,outputDirectory + "/solution.csv");
		}else if (gaVariant == GAVariant.MO){
			problem = new MultiObjectivePrioritizationProblem(inputDirectory, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType, weightType);
			
			double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
		    mutation = new PermutationSwapMutation (mutationProbability) ;
			
		    selection = new BinaryTournamentSelection<PermutationSolution<?>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<?>>()) ;
			
		    algorithm = new NSGAII<PermutationSolution<?>>(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
		    algorithm.run();
		    List<PermutationSolution<?>> pareto = (List<PermutationSolution<?>>) algorithm.getResult();
		    
//		    GAUtils.printNamedSolutions(pareto, "pareto_solutions.csv");
//			GAUtils.printFinalSolutionSet(pareto, "pareto");
			GAUtils.printParetoFrontWithLabels(pareto, outputDirectory + "/pareto.csv");
		}else{
			throw new RuntimeException("Unrecognized algorithm mariant: " + gaVariant + ". Only SO or MO allowed.");
		}

	}
}
