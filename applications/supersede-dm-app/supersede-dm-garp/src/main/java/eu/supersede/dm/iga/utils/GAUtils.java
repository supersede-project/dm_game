/**
 * 
 */
package eu.supersede.dm.iga.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.ode.AbstractParameterizable;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;

/**
 * @author fitsum
 *
 */
public class GAUtils {
	public static void printNamedSolutions(
			List<PermutationSolution<?>> population, String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);

			if (population.size() > 0) {
				Iterator<PermutationSolution<?>> iterator = population
						.iterator();
				while (iterator.hasNext()) {
					PrioritizationSolution solution = (PrioritizationSolution) iterator
							.next();
					bw.write(solution.toNamedString());
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printFinalSolutionSet(
			List<? extends Solution<?>> population, String baseName)
			throws IOException {
		SolutionSetOutput solutionOutput = new SolutionSetOutput();
		solutionOutput.printVariablesToFile(population, baseName + "_VAR.tsv");
		solutionOutput.printObjectivesToFile(population, baseName + "_FUN.tsv");

		JMetalLogger.logger.info("Random seed: "
				+ JMetalRandom.getInstance().getSeed());
		JMetalLogger.logger
				.info("Objectives values have been written to file FUN.tsv");
		JMetalLogger.logger
				.info("Variables values have been written to file VAR.tsv");
	}

	public static void printParetoFrontWithLabels(List<PermutationSolution<?>> population, String path) {
		FileOutputContext outputContext = new DefaultFileOutputContext(path);
		BufferedWriter bufferedWriter = outputContext.getFileWriter();
		String header = "";
		for (String criterion : AbstractPrioritizationProblem.CRITERIA_IDS){
			header += criterion + ",";
		}
		header += "ranking";
		try {
			bufferedWriter.write(header);
			bufferedWriter.newLine();
			// convert to set, for removing duplicates
			Set<PermutationSolution<?>> unique = new HashSet<PermutationSolution<?>>();
			unique.addAll(population);
			for (PermutationSolution<?> sol : unique) {
				PrioritizationSolution solution = (PrioritizationSolution) sol;
				bufferedWriter.write(solution.toNamedStringWithObjectives());
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param solution
	 * @param path
	 */
	public static void printSolutionWithLabels(PrioritizationSolution solution, String path) {
		FileOutputContext outputContext = new DefaultFileOutputContext(path);
		BufferedWriter bufferedWriter = outputContext.getFileWriter();
		String header = "fitness,ranking";
		try {
			bufferedWriter.write(header);
			bufferedWriter.newLine();
			bufferedWriter.write(solution.toNamedStringWithObjectives());
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
