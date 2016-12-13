package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.problem.MultiObjectivePrioritizationProblem;
import eu.supersede.dm.iga.problem.SingleObjectivePrioritizationProblem;

public class IGAAlgorithm {
	
	SortedMap<String, String[]> criteria = new TreeMap<String, String[]>();
	Map<String, String> requirements = new HashMap<>();
	Map<String,Set<String>> dependencies = new HashMap<>();
	Map<String,Double> criteriaWeights = new HashMap<>();
	Map<String, Map<String, Double>> playerWeights = new HashMap<String, Map<String, Double>>();
	Map<String, Map<String, List<String>>> rankings = new HashMap<String, Map<String, List<String>>>();
	
	public void setCriteria( List<String> criteria ) {
		this.criteria.clear();
		criteriaWeights.clear();
		for (String criterion : criteria){
			String[] criterionDetail = {criterion, "min"};
			this.criteria.put( criterion,  criterionDetail);
			criteriaWeights.put( criterion, 1.0 );
		}
	}
	
	public void setCriteriaWeight( String req, Double w ) {
		criteriaWeights.put( req, w );
	}
	
	public void addRequirement( String req, List<String> deps ) {
		requirements.put( req, req );
		Set<String> list = dependencies.get( req );
		if( list != null ) {
			list.clear();
		}
		else {
			list = new HashSet<>();
			dependencies.put( req, list );
		}
		list.addAll( deps );
	}
	
	public List<Map<String,Double>> calc() {
		ArrayList<Map<String,Double>> finalRanking = new ArrayList<Map<String,Double>>();
		double crossoverProbability = 0.9 ;
	    CrossoverOperator crossover = new PMXCrossover(crossoverProbability) ;

	    int searchBudget = 10000;
	    int populationSize = 50;
	    
	    SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<PermutationSolution<?>>();
	    SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection;
	    
	    AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm;
	    MutationOperator<PermutationSolution<?>> mutation;
	    AbstractPrioritizationProblem problem;

	    ObjectiveFunction of = ObjectiveFunction.CRITERIA;
	    GAVariant gaVariant = GAVariant.MO;
	    if (gaVariant == GAVariant.MO){
			problem = new MultiObjectivePrioritizationProblem(criteria, criteriaWeights, playerWeights, requirements, dependencies, rankings, of, gaVariant);
	    }else{
	    	problem = new SingleObjectivePrioritizationProblem(criteria, criteriaWeights, playerWeights, requirements, dependencies, rankings, of, gaVariant);
	    }

	    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    mutation = new PermutationSwapMutation (mutationProbability) ;
	    
	    if (gaVariant == GAVariant.MO){
		    selection = new BinaryTournamentSelection<PermutationSolution<?>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<?>>()) ;
		    algorithm = new NSGAII<PermutationSolution<?>>(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
		    algorithm.run();
		    List<PermutationSolution<?>> pareto = (List<PermutationSolution<?>>) algorithm.getResult();
		    for (PermutationSolution<?> s : pareto){
		    	finalRanking.add(((PrioritizationSolution)s).toRanks());
		    }
		}else{
			selection = new BinaryTournamentSelection<PermutationSolution<?>>(new ObjectiveComparator<PermutationSolution<?>>(0)) ;
		    algorithm = new GenerationalGeneticAlgorithm(problem, searchBudget, populationSize, crossover, mutation, selection, evaluator);
			algorithm.run();
			PrioritizationSolution solution = (PrioritizationSolution)algorithm.getResult();
			System.out.println(solution.toNamedStringWithObjectives());
			finalRanking.add(solution.toRanks());
		}
		
		return finalRanking;
	}
	
	/**
	 * 
	 * @param player: the player (name or some ID)
	 * @param playerRanking: ranking of a player with respect to the various criteria 
	 */
	public void addRanking( String player, Map<String, List<String>> playerRanking ) {
		this.rankings.put( player, playerRanking );
	}
	
	/**
	 * 
	 * @param criterion: name/ID of criterion
	 * @param weights: Map<Player, Weight>
	 */
	public void addPlayerRanking (String criterion, Map<String, Double> weights){
		playerWeights.put(criterion, weights);
	}
	
}
