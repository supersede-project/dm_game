package eu.supersede.dm.iga.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import com.opencsv.CSVReader;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;

//import eu.supersede.dm.algorithms.AHPStructure;
//import eu.supersede.dm.algorithms.Ahp;

public class Utils {
	public static double[][] readCostMatrix(String matrixFile,
			int numRequirements, int numObjectives) {
		double[][] matrix = new double[numRequirements][numObjectives];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					matrixFile)));
			int row = 0;
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty() || line.matches("[a-zA-Z]+.*$")) { // ;[a-zA-Z]+;[a-zA-Z]+;[a-zA-Z]+"))
																		// {
					continue;
				} else {
					// COST,VALUE
					String[] values = line.split(";");
					for (int col = 0; col < values.length; col++) {
						try {
							matrix[row][col] = Double.parseDouble(values[col]);
						} catch (NumberFormatException e) {

							matrix[row][col] = NumberFormat
									.getInstance(Locale.ITALIAN)
									.parse(values[col]).doubleValue();
						}
					}
					row++;
				}

			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrix;
	}

	public static <T> Map<T, Set<T>> readDependencies (String dependencyFile) {
		Map<T, Set<T>> dependencies = new HashMap<T, Set<T>>();
		// Set<Integer> d5 = new HashSet<Integer> ();
		// d5.add(2);
		// d5.add(4);
		// dependencies.put(5, d5);
		//
		// Set<Integer> d2 = new HashSet<Integer> ();
		// d2.add(1);
		// dependencies.put(2, d2);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					dependencyFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty() || line.matches("[a-zA-Z]+->[a-zA-Z]+")) {
					continue;
				} else {
					// REQ, DEP
					String[] values = line.split("->");
					T requirement = (T)values[0];
					Set<T> deps = new HashSet<T>();
					for (String dep : values[1].split(",")) {
						deps.add((T)dep);
					}
					dependencies.put(requirement, deps);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dependencies;
	}

	public static Map<String, String[]> readCriteria1(String criteriaFile) {
		Map<String, String[]> criteria = new HashMap<String, String[]>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					criteriaFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				} else {
					// criteria, desc, min/max
					String[] values = line.split(",");
					String[] vals = new String[2];
					vals[0] = values[1];
					vals[1] = values[2];
					criteria.put(values[0], vals);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return criteria;
	}

	public static SortedMap<String, String[]> readCriteria(String criteriaFile){
		SortedMap<String, String[]> criteria = new TreeMap<String, String[]>();
		Reader reader;
		try {
			reader = new FileReader(criteriaFile);
			CSVReader csvReader = new CSVReader(reader);
			List<String[]> allContent = csvReader.readAll();
			csvReader.close();
			for (int i = 1; i < allContent.size(); i++){
				String[] line = allContent.get(i); // first line skipped (i starts from 1) b/c it's header
				if (line.length == 3){
					String[] vals = new String[2];
					vals[0] = line[1];
					vals[1] = line[2];
					criteria.put(line[0], vals);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to read criteria file: " + criteriaFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected problem while reading criteria file: " + criteriaFile);
		}
		return criteria;
	}
	
	public static Map<String, Double> readCriteriaWeights(String weightsFile) {
		Map<String, Double> criteriaWeights = new HashMap<String, Double>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					weightsFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				} else {
					// c1, w1
					// c2, w2
					String[] values = line.split(",");
					criteriaWeights.put(values[0],
							Double.parseDouble(values[1]));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return criteriaWeights;
	}

	public static Map<String, double[]> readPlayerWeights(String weightsFile) {
		Map<String, double[]> playerWeights = new HashMap<String, double[]>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					weightsFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				} else {
					// Criteria, w1, w2, w3, ...
					String[] values = line.split(",");
					String criterion = values[0];

					double[] weights = new double[values.length - 1];
					for (int i = 1; i < values.length; i++) {
						double r = Double.parseDouble(values[i]);
						weights[i - 1] = r;
					}

					playerWeights.put(criterion, weights);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return playerWeights;
	}

	public static <T> Map<T, List<T>> readPlayerRankings(String rankingsFile) {
		Map<T, List<T>> playerRankings = new HashMap<T, List<T>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					rankingsFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty()) { // ||
										// line.matches("[a-zA-Z]+;[a-zA-Z]+;[a-zA-Z]+"))
										// {
					continue;
				} else {
					// Criteria, R1, R2, R3, ...
					String[] values = line.split(",");
					T criterion = (T)values[0];

					List<T> ranks = new ArrayList<T>(); //T[values.length - 1];
					for (int i = 1; i < values.length; i++) {
						T r = (T)values[i];
						ranks.add(r);
						//ranks[i - 1] = r;
					}

					playerRankings.put(criterion, ranks);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return playerRankings;
	}

	public static Map<Integer, Map<Integer, Set<Integer>>> readPriorities(
			int numRequirements, String prioritiesFile) {
		Map<Integer, Map<Integer, Set<Integer>>> priorities = new HashMap<Integer, Map<Integer, Set<Integer>>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					prioritiesFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty()
						|| line.matches("[a-zA-Z]+;[a-zA-Z]+;[a-zA-Z]+")) {
					continue;
				} else {
					// CLIENT, REQ, DEP
					String[] values = line.split(";");
					int client = Integer.parseInt(values[0]);

					int req = Integer.parseInt(values[1]);

					Set<Integer> deps = new HashSet<Integer>();
					for (String dep : values[2].split("-")) {
						deps.add(Integer.parseInt(dep));
					}

					if (priorities.containsKey(client)) {
						priorities.get(client).put(req, deps);
					} else {
						Map<Integer, Set<Integer>> priority = new HashMap<Integer, Set<Integer>>();
						priority.put(req, deps);
						priorities.put(client, priority);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return priorities;
	}

	public static void writeStringToFile(String content, String fileName) {
		FileWriter out;
		try {
			out = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(out);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Integer> arrayToList(int[] arr) {
		List<Integer> l = new ArrayList<Integer>();
		for (int a : arr) {
			l.add(a);
		}
		return l;
	}

	public static <T> void printArray(List<T> array) {
		for (T v : array){
			System.out.print(v + ", ");
		}
		System.out.println();
	}
	
	public static <T> void printArray(T[] array) {
		for (T v : array){
			System.out.print(v + ", ");
		}
		System.out.println();
	}

	public static void printArray(int[] array){
		for (int v : array){
			System.out.print(v + " ");
		}
		System.out.println();
	}

	public static List<String> readRequirementsSimple(String requirementsFile) {
		List<String> reqs = new ArrayList<String>();
		FileReader fr;
		try {
			fr = new FileReader(requirementsFile);
			BufferedReader br = new BufferedReader(fr);
			while(br.ready()){
				String req = br.readLine();
				if (req != null && !req.trim().isEmpty()){
					reqs.add(req.trim());
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to read requirements file: " + requirementsFile);
		} 
		return reqs;
	}
	
	public static Map<String, String> readRequirements(String requirementsFile){
		Map<String, String> requirements = new HashMap<String, String>();
		Reader reader;
		try {
			reader = new FileReader(requirementsFile);
			CSVReader csvReader = new CSVReader(reader);
			List<String[]> allContent = csvReader.readAll();
			csvReader.close();
			for (int i = 1; i < allContent.size(); i++){
				String[] line = allContent.get(i); // first line skipped (i starts from 1) b/c it's header
				if (line.length == 2){
					requirements.put(line[0], line[1]); // id,description
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to read requirements file: " + requirementsFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected problem while reading requirements file: " + requirementsFile);
		}
		return requirements;
	}
	
	public static void readFinalAhpRanking(String ahpRankingFile, PrioritizationSolution solution){
		List<String> ahpRanking = readRequirementsSimple(ahpRankingFile);
		int[] ranking = AbstractPrioritizationProblem.requirementsListToRanking(ahpRanking);
		for (int i = 0; i < ranking.length; i++){
			solution.setVariableValue(i, ranking[i]);
		}
	}
}
