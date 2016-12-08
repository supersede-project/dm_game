/**
 * 
 */
package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.WeightType;
import eu.supersede.dm.iga.problem.MultiObjectivePrioritizationProblem;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.DistanceType;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.problem.SingleObjectivePrioritizationProblem;
import eu.supersede.dm.iga.utils.GAUtils;
import eu.supersede.dm.iga.utils.Utils;

/**
 * @author fitsum
 *
 */
public class AhpExperimentMain {

	String ahpVotesFileBase = "resources/input/VOTES/";
	String dependenciesFile = "resources/input/dependencies";
	String playerWeightsFile = "resources/input/weights_player_presto.csv"; 
	String criteriaWeightsFile = "resources/input/weights_criteria_presto.csv";
	String outputBase = "output/";
	ObjectiveFunction of = ObjectiveFunction.CRITERIA;
	GAVariant gaVariant = GAVariant.MO;
	
	static Map<String, double[]> playerWeights;
	
	public static String SUBSYSTEM = "";
	
	public static void main(String[] args) {
		if (args.length == 0){
			System.err.println("Please enter the subsystem name (Timeline, System, or SceneSelection), distance type (kendall, spearman, delta), and weight type (RANDOM, EQUAL, INPUT, defaults to EQUAL).");
			System.exit(0);
		}
//		String subSystem = args[0];
		SUBSYSTEM = args[0];
		DistanceType distanceType = DistanceType.valueOf(args[1].toUpperCase());;
		WeightType weightType = WeightType.EQUAL;
		if (args.length == 3){
			weightType = WeightType.valueOf(args[2].toUpperCase());
		}
		AhpExperimentMain experiment = new AhpExperimentMain();
		experiment.runMOGA(distanceType, weightType);
	}
	
	public PrioritizationSolution runSOGA(DistanceType distanceType, WeightType weightType, AbstractPrioritizationProblem moProblem){
		GAVariant gaVariant = GAVariant.SO;
		double crossoverProbability = 0.7 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
		
		SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    
	    Problem<PermutationSolution<?>> problem = new SingleObjectivePrioritizationProblem(ahpVotesFileBase + SUBSYSTEM + ".csv", dependenciesFile, of, gaVariant, distanceType, weightType, playerWeightsFile, criteriaWeightsFile);
		
	    // take weights from the multi-objective problem
	    if (moProblem != null){
	    	((AbstractPrioritizationProblem)problem).setPlayerWeights(moProblem.getPlayerWeights());
	    	((AbstractPrioritizationProblem)problem).setCriteriaWeights(moProblem.getCriteriaWeights());
	    }
	    
	    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    MutationOperator<PermutationSolution<?>> mutation = new PermutationSwapMutation (mutationProbability);
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection = new BinaryTournamentSelection<PermutationSolution<?>>(new ObjectiveComparator<PermutationSolution<?>>(0)) ;
		
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm = new GenerationalGeneticAlgorithm(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
	    algorithm.run();
	    PrioritizationSolution solution = (PrioritizationSolution)algorithm.getResult();
	    return solution;
	}

	/**
	 * 
	 */
	public void runMOGA(DistanceType distanceType, WeightType weightType) {
		
		GAVariant gaVariant = GAVariant.MO;

	    double crossoverProbability = 0.7 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
		
		SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    
	    Problem<PermutationSolution<?>> problem = new MultiObjectivePrioritizationProblem(ahpVotesFileBase + SUBSYSTEM + ".csv", dependenciesFile, of, gaVariant, distanceType, weightType, playerWeightsFile, criteriaWeightsFile);
		
	    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    MutationOperator<PermutationSolution<?>> mutation = new PermutationSwapMutation (mutationProbability);
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection = new BinaryTournamentSelection<PermutationSolution<?>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<?>>()) ;
		
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm = new NSGAII<PermutationSolution<?>>(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
	    algorithm.run();
	    List<PermutationSolution<?>> pareto = (List<PermutationSolution<?>>) algorithm.getResult();
	    GAUtils.printParetoFrontWithLabels(pareto, outputBase + SUBSYSTEM + "_" + distanceType.toString() + "_" + weightType.toString() + "_pareto.csv");
	    
	    List<PermutationSolution<?>> ahpPareto = new ArrayList<PermutationSolution<?>>();
	    String ahpRankingFileAverage = ahpVotesFileBase + SUBSYSTEM + "_ahp_average.csv";
	    PrioritizationSolution ahpSolutionAverage = (PrioritizationSolution) problem.createSolution();
	    Utils.readFinalAhpRanking(ahpRankingFileAverage, ahpSolutionAverage);
	    problem.evaluate(ahpSolutionAverage);
	    ahpPareto.add(ahpSolutionAverage);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + SUBSYSTEM + "_" + distanceType.toString() + "_" + weightType.toString() + "_ahp_pareto_average.csv");
	    
	    String ahpRankingFileNegotiator = ahpVotesFileBase + SUBSYSTEM + "_ahp_negotiator.csv";
	    PrioritizationSolution ahpSolutionNegotiator = (PrioritizationSolution) problem.createSolution();
	    Utils.readFinalAhpRanking(ahpRankingFileNegotiator, ahpSolutionNegotiator);
	    problem.evaluate(ahpSolutionNegotiator);
	    ahpPareto.clear();
	    ahpPareto.add(ahpSolutionNegotiator);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + SUBSYSTEM + "_" + distanceType.toString() + "_" + weightType.toString() + "_ahp_pareto_negotiator.csv");
	    
	    // Get a solution using the single-objective ga
	    AhpExperimentMain ahpExp = new AhpExperimentMain();
	    PrioritizationSolution sogaSolution = ahpExp.runSOGA(distanceType, weightType, (AbstractPrioritizationProblem)problem);
	    PrioritizationSolution mogaSolution = (PrioritizationSolution) problem.createSolution();
	    for (int i = 0; i < sogaSolution.getNumberOfVariables(); i++){
	    	mogaSolution.setVariableValue(i, sogaSolution.getVariableValue(i));
	    }
	    problem.evaluate(mogaSolution);
	    ahpPareto.clear();
	    ahpPareto.add(mogaSolution);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + SUBSYSTEM + "_" + distanceType.toString() + "_" + weightType.toString() + "_soga_pareto.csv");
	}

}
