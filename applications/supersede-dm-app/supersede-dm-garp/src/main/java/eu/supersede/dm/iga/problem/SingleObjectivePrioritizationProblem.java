/**
 * 
 */
package eu.supersede.dm.iga.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.DistanceType;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;

/**
 * @author fitsum
 *
 */
public class SingleObjectivePrioritizationProblem extends AbstractPrioritizationProblem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6009605359050279552L;

	public SingleObjectivePrioritizationProblem(String ahpVotesFile, String dependenciesFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType){
		super(ahpVotesFile, dependenciesFile, of, gaVariant, distanceType);
		problemName = "SingleObjectivePrioritizationProblem";
	}
	
	
	/**
	 * 
	 */
	public SingleObjectivePrioritizationProblem(int numPlayers,
			String criteriaFile, String dependenciesFile,
			String criteriaWeightFile, String playerWeightFile,
			String requirementsFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType) {
		super(numPlayers, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile, of, gaVariant, distanceType);
		problemName = "SingleObjectivePrioritizationProblem";
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.uma.jmetal.problem.Problem#evaluate(org.uma.jmetal.solution.Solution)
	 */
	public void evaluate(PermutationSolution<?> solution) {
		Double fitness = fitnessCache.get(solution.hashCode());
		if (fitness != null){
			solution.setObjective(0, fitness);
		}else{
			if (violatesDependencyConstraints(solution)){
				solution.setObjective(0, Double.MAX_VALUE);
				fitnessCache.put(solution.hashCode(), Double.MAX_VALUE);
			}else{
				evaluateSingleObjective ((PrioritizationSolution) solution);
				fitnessCache.put(solution.hashCode(), solution.getObjective(0));
			}
		}

	}
	
	
	/**
	 * Objective function corresponds to the weighted average of all distances
	 * @param solution
	 * @throws JMException
	 */
	private void evaluateSingleObjective(PrioritizationSolution solution){

//		int[] individual = ((PrioritizationSolution) solution).getVariablesArray();

		double d = 0.0;
		int idx = 0;
		for (String key : criteria.keySet()){
			double cw = 1d / criteriaWeights.get(key);
			for (int p = 0; p < numberOfPlayers; p++){
				double pw = 1d / playerWeights.get(key)[p];
				List<String> pr = playerRankings.get(p).get(key);
				double dist = computeDistance(solution, pr);
				d += cw * pw * dist;
			}
		}
		d /= numberOfPlayers * criteria.size();
		solution.setObjective(idx, d);

	}

}
