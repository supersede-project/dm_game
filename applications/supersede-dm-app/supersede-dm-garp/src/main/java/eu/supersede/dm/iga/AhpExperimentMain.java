/**
 * 
 */
package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.List;

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
	String outputBase = "output/";
	ObjectiveFunction of = ObjectiveFunction.CRITERIA;
	GAVariant gaVariant = GAVariant.MO;
	
	public static void main(String[] args) {
		if (args.length == 0){
			System.err.println("Please enter the subsystem name (Timeline, System, or SceneSelection), and an optional distance (kendall, spearman, delta), defaults to kendall.");
			System.exit(0);
		}
		String subSystem = args[0];
		DistanceType distanceType = DistanceType.KENDALL;
		if (args.length == 2){
			distanceType = DistanceType.valueOf(args[1].toUpperCase());
		}
		AhpExperimentMain experiment = new AhpExperimentMain();
		experiment.runMOGA(subSystem, distanceType);
	}
	
	public PrioritizationSolution runSOGA(String subSystem, DistanceType distanceType){
		GAVariant gaVariant = GAVariant.SO;
		double crossoverProbability = 0.7 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
		
		SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    
	    Problem<PermutationSolution<?>> problem = new SingleObjectivePrioritizationProblem(ahpVotesFileBase + subSystem + ".csv", dependenciesFile, of, gaVariant, distanceType);
		
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
	public void runMOGA(String subSystem, DistanceType distanceType) {
		
		GAVariant gaVariant = GAVariant.MO;

	    double crossoverProbability = 0.7 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
		
		SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    
	    Problem<PermutationSolution<?>> problem = new MultiObjectivePrioritizationProblem(ahpVotesFileBase + subSystem + ".csv", dependenciesFile, of, gaVariant, distanceType);
		
	    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    MutationOperator<PermutationSolution<?>> mutation = new PermutationSwapMutation (mutationProbability);
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection = new BinaryTournamentSelection<PermutationSolution<?>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<?>>()) ;
		
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm = new NSGAII<PermutationSolution<?>>(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
	    algorithm.run();
	    List<PermutationSolution<?>> pareto = (List<PermutationSolution<?>>) algorithm.getResult();
	    GAUtils.printParetoFrontWithLabels(pareto, outputBase + subSystem + "_" + distanceType.toString() + "_pareto.csv");
	    
	    List<PermutationSolution<?>> ahpPareto = new ArrayList<PermutationSolution<?>>();
	    String ahpRankingFileAverage = ahpVotesFileBase + subSystem + "_ahp_average.csv";
	    PrioritizationSolution ahpSolutionAverage = (PrioritizationSolution) problem.createSolution();
	    Utils.readFinalAhpRanking(ahpRankingFileAverage, ahpSolutionAverage);
	    problem.evaluate(ahpSolutionAverage);
	    ahpPareto.add(ahpSolutionAverage);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + subSystem + "_" + distanceType.toString() + "_ahp_pareto_average.csv");
	    
	    String ahpRankingFileNegotiator = ahpVotesFileBase + subSystem + "_ahp_negotiator.csv";
	    PrioritizationSolution ahpSolutionNegotiator = (PrioritizationSolution) problem.createSolution();
	    Utils.readFinalAhpRanking(ahpRankingFileNegotiator, ahpSolutionNegotiator);
	    problem.evaluate(ahpSolutionNegotiator);
	    ahpPareto.clear();
	    ahpPareto.add(ahpSolutionNegotiator);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + subSystem + "_" + distanceType.toString() + "_ahp_pareto_negotiator.csv");
	    
	    // Get a solution using the single-objective ga
	    AhpExperimentMain ahpExp = new AhpExperimentMain();
	    PrioritizationSolution sogaSolution = ahpExp.runSOGA(subSystem, distanceType);
	    PrioritizationSolution mogaSolution = (PrioritizationSolution) problem.createSolution();
	    for (int i = 0; i < sogaSolution.getNumberOfVariables(); i++){
	    	mogaSolution.setVariableValue(i, sogaSolution.getVariableValue(i));
	    }
	    problem.evaluate(mogaSolution);
	    ahpPareto.clear();
	    ahpPareto.add(mogaSolution);
	    GAUtils.printParetoFrontWithLabels(ahpPareto, outputBase + subSystem + "_" + distanceType.toString() + "_soga_pareto.csv");
	}

}
