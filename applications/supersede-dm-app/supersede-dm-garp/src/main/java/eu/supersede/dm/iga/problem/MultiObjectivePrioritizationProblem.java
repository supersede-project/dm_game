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
			String requirementsFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType) {
		super(numPlayers, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType);
		problemName = "MultiObjectivePrioritizationProblem";
	}


	/**
	 * @param ahpVotesFile
	 * @param dependenciesFile
	 * @param of
	 * @param gaVariant
	 */
	public MultiObjectivePrioritizationProblem(String ahpVotesFile,
			String dependenciesFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType) {
		super(ahpVotesFile, dependenciesFile, of, gaVariant, distanceType);
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
		for (String key : criteria.keySet()){
			double cw = 1d / criteriaWeights.get(key);
			for (int p = 0; p < numberOfPlayers; p++){
				double pw = 1d / playerWeights.get(key)[p];
				List<String> pr = playerRankings.get(p).get(key);
				double dist = computeDistance(solution, pr);
				d += pw * dist;
			}
			d *= cw;
			d /= numberOfRequirements;
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
		
		for (int p = 0; p < numberOfPlayers; p++){
			for (String key : criteria.keySet()){
				double pw = 1d / playerWeights.get(key)[p];
				double cw = 1d / criteriaWeights.get(key);
				List<String> pr = playerRankings.get(p).get(key);
				double dist = computeDistance(solution, pr);
				d += (cw * pw * dist);
			}
			d /= numberOfRequirements;
			d /= criteria.keySet().size();
			solution.setObjective(p, d);
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
			for (int i = 0; i < criteria.keySet().size(); i++){
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
