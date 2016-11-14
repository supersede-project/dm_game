/**
 * 
 */
package eu.supersede.dm.iga.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import eu.supersede.dm.algorithms.AHPStructure;
import eu.supersede.dm.algorithms.Ahp;
import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.utils.MapUtil;
import eu.supersede.dm.iga.utils.StatisticsUtils;
import eu.supersede.dm.iga.utils.Utils;


/**
 * @author fitsum
 *
 */
public abstract class AbstractPrioritizationProblem implements PermutationProblem<PermutationSolution<?>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1781428946819675177L;
	
	public int numberOfRequirements;
	public int numberOfPlayers;

	int numberOfVariables = 1;
	int numberOfObjectives = 1;
	int numberOfConstraints = 0;
	String problemName = "";
	
	// ranking of each player with respect to each criteria
	protected List<Map<String, List<String>>> playerRankings;
	
	// dependencies among requirements
	protected Map<String,Set<String>> dependencies;
	
	// weights of players
	protected Map<String, double[]> playerWeights;
	
	// weights of the criteria
	protected Map<String, Double> criteriaWeights;
	
	protected Map<String, String[]> criteria;
	
	protected Map<String, String> requirements;
	
	public static List<String> REQUIREMENT_IDS = new ArrayList<String>();
	
	public enum ObjectiveFunction{
		PLAYERS, CRITERIA
	}
	
	public enum GAVariant{
		SO, MO
	}
	
	public enum DistanceType{
		KENDALL, DELTA, DELTAINDEX, SPEARMAN, PEARSON
	}
	
	public static GAVariant GA_VARIANT;
	
	public static ObjectiveFunction OBJECTIVE_FUNCTION = ObjectiveFunction.PLAYERS;
	
	public static DistanceType DISTANCE_TYPE = DistanceType.KENDALL;
	
	
	/**
	 * store already 'seen' permutations to avoid duplicates
	 */
	public static Set<Integer> seenSolutions = new HashSet<Integer>();

	/*
	 * store fitness evaluations for caching purposes
	 */
	protected static Map<Integer, Double> fitnessCache = new HashMap<Integer, Double>();
	
	/**
	 * 
	 */
	public AbstractPrioritizationProblem() {
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * Read the prioritization problem from AHP vote files 
	 */
	public AbstractPrioritizationProblem(String ahpVotesFile, String dependenciesFile, ObjectiveFunction of, GAVariant gaVariant) {
		readProblemFromAHP(ahpVotesFile, dependenciesFile);
		numberOfVariables = numberOfRequirements;
		OBJECTIVE_FUNCTION = of;
		if (gaVariant == GAVariant.MO){
			if (OBJECTIVE_FUNCTION == ObjectiveFunction.CRITERIA){
				numberOfObjectives = criteria.size();
			}else if (OBJECTIVE_FUNCTION == ObjectiveFunction.PLAYERS){
				numberOfObjectives = numberOfPlayers;
			}
		}else{
			numberOfObjectives = 1;
		}
		numberOfConstraints = 0;

	}

	
	public AbstractPrioritizationProblem(int numPlayers, String criteriaFile, String dependenciesFile, String criteriaWeightFile, String playerWeightFile, String requirementsFile, ObjectiveFunction of, GAVariant gaVariant) {
		OBJECTIVE_FUNCTION = of;
		GA_VARIANT = gaVariant;
		numberOfPlayers = numPlayers;
		readProblem(criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile);
		numberOfRequirements = requirements.size();
		numberOfVariables = numberOfRequirements;
		if (GA_VARIANT == GAVariant.MO){
			if (OBJECTIVE_FUNCTION == ObjectiveFunction.CRITERIA){
				numberOfObjectives = criteria.size();
			}else if (OBJECTIVE_FUNCTION == ObjectiveFunction.PLAYERS){
				numberOfObjectives = numberOfPlayers;
			}
		}else{
			numberOfObjectives = 1;
		}
		numberOfConstraints = 0;


		System.out.println(numberOfRequirements);
	}

	/** read the instance of the problem. In particular, read the following aspects of the problem instance:
	 * Player rankings per each criteria
	 * Criteria for comparison
	 * Player weights for each criteria
	 * Weights for each criteria
	 * Dependencies among requirements
	 * @param playerWeightFile 
	 * @param criteriaWeightFile 
	 * @param dependenciesFile 
	 */
	
	private void readProblem (String criteriaFile, String dependenciesFile, String criteriaWeightFile, String playerWeightFile, String requirementsFile){
		criteria = Utils.readCriteria(criteriaFile);
		criteriaWeights = Utils.readCriteriaWeights(criteriaWeightFile);
		String rankingsFile = "resources/input/rankings_p__NUM__.csv";
		playerRankings = new ArrayList<Map<String, List<String>>> ();
		for (int i = 0; i < numberOfPlayers; i++){
			Map<String, List<String>> rankings = Utils.readPlayerRankings(rankingsFile.replace("__NUM__", ""+(i+1)));
			playerRankings.add(rankings);
		}
		dependencies = Utils.readDependencies(dependenciesFile);
		playerWeights = Utils.readPlayerWeights(playerWeightFile);
		requirements = Utils.readRequirements(requirementsFile);
		REQUIREMENT_IDS.addAll(requirements.keySet());
		Collections.sort(REQUIREMENT_IDS);
	}

	private void readProblemFromAHP (String ahpVotesFile, String dependenciesFile){
		criteria = new HashMap<String, String[]>();
		criteriaWeights = new HashMap<String, Double>();
		playerWeights = new HashMap<String, double[]>();
		playerRankings = new ArrayList<Map<String, List<String>>> ();
		requirements = new HashMap<String, String>();
		
		
//		Map<String, Map<String, int[]>> ahpRanks = Utils.readAHPRanks(ahpVotesFile, criteria, criteriaWeights, playerWeights);
		Map<String, Map<String, String[]>> playerRanks = new HashMap<String, Map<String, String[]>>();

		Set<String> requirementSet = new HashSet<String>();
		Set<String> criteriaValues = new HashSet<String>();

		List<String[]> matrix = new ArrayList<String[]>();
		Map<String, List<String[]>> playerVotes = new HashMap<String, List<String[]>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(ahpVotesFile)));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.isEmpty()
						|| line.startsWith("REQUIREMENT1;REQUIREMENT2;CRITERIA;USER;VOTE;VOTE_TIME")) {
					continue;
				} else {
					// each line has following format
					// REQUIREMENT1;REQUIREMENT2;CRITERIA;USER;VOTE;VOTE_TIME

					String[] values = line.split(";");
					String req1 = values[0];
					String req2 = values[1];
					String criterion = values[2];
					String player = values[3];
					String vote = values[4];
					String time = values[5];

					requirementSet.add(req1);
					requirementSet.add(req2);
					criteriaValues.add(criterion);

					String[] row = new String[4];
					row[0] = req1;
					row[1] = req2;
					row[2] = criterion;
					row[3] = vote;

					matrix.add(row);
					
					if (playerVotes.containsKey(player)){
						playerVotes.get(player).add(row);
					}else{
						List<String[]> m = new ArrayList<String[]>();
						m.add(row);
						playerVotes.put(player, m);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// put the requirements from set to list
		List<String> requirementList = new ArrayList<String>();
//		String[] arr = new String[requirementSet.size()];
		requirementList.addAll(requirementSet);
		Collections.sort(requirementList);
		
		// all players have equal weights for all criteria
		double[] defaultWeights = new double[playerVotes.keySet().size()];
		for (int i = 0; i < defaultWeights.length; i++){
			defaultWeights[i] = 10;
		}

		int ci = 1;
		for (String c : criteriaValues){
			String criterionId = "C" + ci ++;
			String[] criterionDetail = {c, "min"};
			criteria.put(criterionId, criterionDetail);

			playerWeights.put(criterionId, defaultWeights);
			
			criteriaWeights.put(criterionId, new Double(10)); // criteria have equal weight in this case
		}
		
		// for each player, and for each criterion, compute the ranks
		for (Entry<String, List<String[]>> entry : playerVotes.entrySet()){
			System.out.println(entry.getKey());
			
			Map<String, String[]> rankCriterion = new HashMap<String, String[]>();
			int j = 1;
			String criterionId;
			for (String criterion : criteriaValues){
				System.out.println(criterion);
//				criterionId = "C" + (j++);
				
				AHPStructure objAHP = new AHPStructure();
				
				// set the criteria
				objAHP.setCriteria(criterion);
				
				// set the alternatives (requirements)
				objAHP.setOptions(requirementList);
				
				// populate the vote matrix
				for (String[] row : entry.getValue()) {
					if (row[2].equalsIgnoreCase(criterion)){
						objAHP.setOptionPreference(row[0], row[1], row[2], Integer.parseInt(row[3]));
					}
				}

				Ahp objCalculateRank = new Ahp(objAHP);

				Map<String, Double> result = objCalculateRank.execute();

				LinkedHashMap<String, Double> sortedRanks = (LinkedHashMap<String, Double>) MapUtil.sortByValue(result);
				
				Map<String, Integer> rankings = MapUtil.ahpRanksToRanking(result);
				
				String[] ranks = new String[sortedRanks.size()];
				int i = 0;
//				for (String req : requirementList){
////					ranks[i++] = rankings.get(req);
//					ranks[i++] = req;
//				}
				
				for (Entry<String, Double> ranking : sortedRanks.entrySet()){
					ranks[i++] = ranking.getKey();
				}
				
				rankCriterion.put(criterion, ranks);
				Utils.printArray(ranks);
				
				
				
//				for (Entry<String, Double> e : sortedRanks.entrySet())
//					System.out.println(e.getKey() + ";" + e.getValue());
			}
			playerRanks.put(entry.getKey(), rankCriterion);

		}
		
		
		for (String req : requirementList){
			requirements.put(req, req);
		}
		
		numberOfPlayers = playerRanks.keySet().size();
		numberOfRequirements = requirements.size();
		REQUIREMENT_IDS.addAll(requirements.keySet());
//		playerRankings.addAll(playerRanks.values()); //TODO uncomment after debug!! change ranking from int[] to String[]
		
//		dependencies = new HashMap<String, Set<String>> ();
		dependencies = Utils.readDependencies(dependenciesFile);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.Problem#createSolution()
	 */
	public PermutationSolution<?> createSolution() {
		PrioritizationSolution solution;
		boolean valid = true;
		int trials = 0;
		do{
			solution = new PrioritizationSolution(this);
			trials ++;
			if (!isUnique(solution) || violatesDependencyConstraints(solution)){
				valid = false;
			}
		}while (!valid && trials <= 10);
		System.err.println(trials + " trails, " + valid);
		registerSeenPermutation(solution);
		return solution;
	}

	
	protected double computeDistance (PrioritizationSolution solution1, List<String> solution2){
		int[] ranking1 = requirementsListToRanking(Arrays.asList(solution1.getVariablesStringArray()));
		int[] ranking2 = requirementsListToRanking(solution2);
		
		return computeDistance(ranking1, ranking2);
	}
	
	/**
	 * @param solution
	 * @return
	 */
	private int[] requirementsListToRanking(List<String> solution) {
		int[] ranking = new int[solution.size()];
		int i = 0;
		for (String reqId : solution){
			ranking[i++] = REQUIREMENT_IDS.indexOf(reqId);
		}
		return ranking;
	}

	protected double computeDistance (int[] ranking1, int[] ranking2){
		switch(DISTANCE_TYPE){
		case KENDALL:
			return StatisticsUtils.distanceKendallTau(ranking1, ranking2);
		case DELTA:
			return StatisticsUtils.distanceDelta(ranking1, ranking2);
		case DELTAINDEX:
			return StatisticsUtils.distanceDeltaIndex(ranking1, ranking2);
		default:
			return StatisticsUtils.distanceDelta(ranking1, ranking2);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.PermutationProblem#getPermutationLength()
	 */
	public int getPermutationLength() {
		return numberOfRequirements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.Problem#getNumberOfVariables()
	 */
	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.Problem#getNumberOfObjectives()
	 */
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.Problem#getNumberOfConstraints()
	 */
	public int getNumberOfConstraints() {
		return numberOfConstraints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uma.jmetal.problem.Problem#getName()
	 */
	public String getName() {
		return problemName;
	}
	
	public boolean violatesDependencyConstraints(PermutationSolution<?> solution) {
		boolean violates = false;
//		int numViolations = 0;
		
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			String r = solution.getVariableValueString(i);
			Set<String> deps = dependencies.get(r);
			if (deps == null || deps.isEmpty()) {
				continue;
			}
			
			// at this point, deps is not empty
			List<String> prefix = Arrays.asList(Arrays.copyOfRange(((PrioritizationSolution)solution).getVariablesStringArray(), 0, i));
			if (prefix.size() == 0){
				violates = true;
				break;
			}
			
			// check the dependencies
			for (String d : deps){
				if (!prefix.contains(d)){
					violates = true;
					break;
//					numViolations ++;
				}
			}
			if (violates){
				break;
			}
		}
//		System.err.println (numViolations + " constraint violations!");
		return violates;
	}

	protected static <T> boolean  contains(T[] arr, T d) {
		for (T v : arr){
			if (v.toString().equalsIgnoreCase(d.toString())){
				return true;
			}
		}
		return false;
	}


	/**
	 * @return the opts
	 */
	public List<String> getOptions() {
		List<String> requirementIds = new ArrayList<String>();
		requirementIds.addAll(requirements.keySet());
		Collections.sort(requirementIds);
		return requirementIds;
	}


	public static boolean isUnique (int[] solution){
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i : solution){
			l.add(i);
		}
		
		return !seenSolutions.contains(l.hashCode());
	}
	
	public static boolean isUnique (PrioritizationSolution solution){
		return isUnique(solution.getVariablesArray());
	}

	public static void registerSeenPermutation(PrioritizationSolution solution) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i : solution.getVariablesArray()){
			l.add(i);
		}
		seenSolutions.add(l.hashCode());
	}
}
