/**
 * 
 */
package eu.supersede.dm.iga.problem;


import java.util.List;

import javax.management.JMException;

import org.uma.jmetal.solution.PermutationSolution;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;

/**
 * @author fitsum
 *
 */
public class MultiObjectivePrioritizationProblem extends AbstractPrioritizationProblem {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6009605359050279552L;

	
	/**
	 * @param numPlayers
	 * @param criteriaFile
	 * @param dependenciesFile
	 * @param criteriaWeightFile
	 * @param playerWeightFile
	 * @param requirementsFile
	 * @param of
	 */
	public MultiObjectivePrioritizationProblem(int numPlayers,
			String criteriaFile, String dependenciesFile,
			String criteriaWeightFile, String playerWeightFile,
			String requirementsFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType, WeightType weightType) {
		super(numPlayers, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType, weightType);
		problemName = "MultiObjectivePrioritizationProblem";
	}


	/**
	 * @param ahpVotesFile
	 * @param dependenciesFile
	 * @param of
	 * @param gaVariant
	 */
	public MultiObjectivePrioritizationProblem(String ahpVotesFile,
			String dependenciesFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType, WeightType weightType, String playerWeightsFile, String criteriaWeightsFile) {
		super(ahpVotesFile, dependenciesFile, of, gaVariant, distanceType, weightType, playerWeightsFile, criteriaWeightsFile);
		problemName = "MultiObjectivePrioritizationProblem";
	}


	/**
	 * Objective functions correspond to the prioritization Criteria
	 * @param solution
	 * @throws JMException
	 */
	private void evaluateOnCriteria(PrioritizationSolution solution){

//		String[] individual = ((RequirementsPrioritizationSolution) solution).getVariablesArray();

		double d = 0.0;
		int idx = 0;
		for (String criterion : getCriteria().keySet()){
			double cw = getCriteriaWeights().get(criterion);
			for (String player : getPlayerRankings().keySet()){
				double pw = getPlayerWeights().get(criterion).get(player);
				List<String> pr = getPlayerRankings().get(player).get(criterion);
				double dist = computeDistance(solution, pr);
				d += pw * dist;
			}
			d *= cw;
			d /= numberOfPlayers; // numberOfRequirements; // TODO WHY was this numberOfRequirements ?!
			solution.setObjective(idx++, d);
		}

	}
	
	/**
	 * Objective functions correspond to each "Player"
	 * @param solution
	 * @throws JMException
	 */
	private void evaluateOnPlayers(PrioritizationSolution solution){

//		int[] individual = ((PrioritizationSolution) solution).getVariablesArray();

		double d = 0.0;
		
		int idx = 0;
		for (String player : getPlayerRankings().keySet()){
			for (String criterion : getCriteria().keySet()){
				double pw = getPlayerWeights().get(criterion).get(player);
				double cw = getCriteriaWeights().get(criterion);
				List<String> pr = getPlayerRankings().get(player).get(criterion);
				double dist = computeDistance(solution, pr);
				d += (cw * pw * dist);
			}
//			d /= numberOfRequirements; // TODO WHY was this added??
			d /= getCriteria().keySet().size();
			solution.setObjective(idx++, d);
		}

	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.uma.jmetal.problem.Problem#evaluate(org.uma.jmetal.solution.Solution)
	 */
	public void evaluate(PermutationSolution<?> solution) {
		if (violatesDependencyConstraints(solution)){
			for (int i = 0; i < getCriteria().keySet().size(); i++){
				solution.setObjective(i, Double.MAX_VALUE);
			}
		}else{
			if (OBJECTIVE_FUNCTION == ObjectiveFunction.CRITERIA){
				evaluateOnCriteria((PrioritizationSolution) solution);
			}else if (OBJECTIVE_FUNCTION == ObjectiveFunction.PLAYERS){
				evaluateOnPlayers((PrioritizationSolution) solution);
			}else{
				throw new RuntimeException("Unknown objective function type: " + OBJECTIVE_FUNCTION);
			}
		}

	}

}
