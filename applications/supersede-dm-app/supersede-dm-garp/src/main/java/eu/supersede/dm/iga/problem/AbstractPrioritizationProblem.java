package eu.supersede.dm.iga.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
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
	private Map<String, Map<String, List<String>>> playerRankings;
	
	// dependencies among requirements
	private Map<String,Set<String>> dependencies;
	
	// weights of players
	private Map<String, Map<String, Double>> playerWeights;
	
	// weights of the criteria
	private Map<String, Double> criteriaWeights;
	
	private SortedMap<String, String[]> criteria;
	
	private Map<String, String> requirements;
	
	public static List<String> REQUIREMENT_IDS = new ArrayList<String>();
	
	public static List<String> CRITERIA_IDS = new ArrayList<String>();
	
	public enum ObjectiveFunction{
		PLAYERS, CRITERIA
	}
	
	public enum GAVariant{
		SO, MO
	}
	
	public enum DistanceType{
		KENDALL, DELTA, DELTAINDEX, SPEARMAN, PEARSON
	}
	
	public enum WeightType{
		RANDOM, EQUAL, INPUT
	}
	
	public static GAVariant GA_VARIANT;
	
	public static ObjectiveFunction OBJECTIVE_FUNCTION = ObjectiveFunction.PLAYERS;
	
	public static DistanceType DISTANCE_TYPE = DistanceType.SPEARMAN; // .DELTA; // .KENDALL;
	
	
	public static WeightType WEIGHT_TYPE = WeightType.INPUT;
	
	/**
	 * store already 'seen' permutations to avoid duplicates
	 */
	public static Set<Integer> seenSolutions = new HashSet<Integer>();

	/*
	 * store fitness evaluations for caching purposes
	 */
	protected static Map<Integer, Double> fitnessCache = new HashMap<Integer, Double>();
	
	private Random rnd = new Random();
	
	public static int MAX_WEIGHT = 20;
	public static int MIN_WEIGHT = 1;
	
	/**
	 * 
	 */
	public AbstractPrioritizationProblem() {
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * Read the prioritization problem from AHP vote files 
	 */
	public AbstractPrioritizationProblem(String ahpVotesFile, String dependenciesFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType, WeightType weightType, String playerWeightsFile, String criteriaWeightsFile) {
		WEIGHT_TYPE = weightType;
//		readProblemFromAHP(ahpVotesFile, dependenciesFile, playerWeightsFile, criteriaWeightsFile);
		DISTANCE_TYPE = distanceType;
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

	
	public AbstractPrioritizationProblem(String inputDir, String criteriaFile, String dependenciesFile, String criteriaWeightFile, String playerWeightFile, String requirementsFile, ObjectiveFunction of, GAVariant gaVariant, DistanceType distanceType, WeightType weightType) {
		WEIGHT_TYPE = weightType;
		DISTANCE_TYPE = distanceType;
		OBJECTIVE_FUNCTION = of;
		GA_VARIANT = gaVariant;
//		numberOfPlayers = numPlayers;
		readProblem(inputDir, criteriaFile, dependenciesFile, criteriaWeightFile, playerWeightFile, requirementsFile);
		
		CRITERIA_IDS.clear();
		CRITERIA_IDS.addAll(criteria.keySet());
		Collections.sort(CRITERIA_IDS);
		
		REQUIREMENT_IDS.clear();
		REQUIREMENT_IDS.addAll(requirements.keySet());
		Collections.sort(REQUIREMENT_IDS);
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

	public AbstractPrioritizationProblem(SortedMap<String, String[]> criteria2, Map<String, Double> criteriaWeights2,
			Map<String, Map<String, Double>> playerWeights2, Map<String, String> requirements2, Map<String, Set<String>> dependencies2,
			Map<String, Map<String, List<String>>> rankings, ObjectiveFunction of, GAVariant gaVariant) {
		
		OBJECTIVE_FUNCTION = of;
		GA_VARIANT = gaVariant;
		
		this.criteria = criteria2;
		this.criteriaWeights = Utils.criteriaWeightsToProbabilities(criteriaWeights2);
		this.playerWeights = Utils.playerWeightsToProbabilities(playerWeights2);
		this.requirements = requirements2;
		this.dependencies = dependencies2;
		this.playerRankings = rankings;
		
		numberOfPlayers = playerRankings.keySet().size(); // playerWeights.get("c1").keySet().size();
		
		CRITERIA_IDS.clear();
		CRITERIA_IDS.addAll(criteria.keySet());
		Collections.sort(CRITERIA_IDS);
		
		REQUIREMENT_IDS.clear();
		REQUIREMENT_IDS.addAll(requirements.keySet());
		Collections.sort(REQUIREMENT_IDS);
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
	
	private void readProblem (String inputDir, String criteriaFile, String dependenciesFile, String criteriaWeightFile, String playerWeightFile, String requirementsFile){
		criteria = Utils.readCriteria(criteriaFile);
		criteriaWeights = Utils.readCriteriaWeights(criteriaWeightFile);
		playerWeights = Utils.playerWeightsToProbabilities(Utils.readPlayerWeights(playerWeightFile));
//		numberOfPlayers = playerWeights.get("c1").keySet().size();
//		String rankingsFile = inputDir + "/ranking_p__NUM__.csv";
		playerRankings = new HashMap<String, Map<String, List<String>>> ();
//		for (int i = 0; i < numberOfPlayers; i++){
		int i = 1;
		for (String rankingsFile : Utils.getFiles(inputDir, "ranking_p")){
			Map<String, List<String>> rankings = Utils.readPlayerRankings(inputDir + "/" + rankingsFile); //.replace("__NUM__", ""+(i+1)));
			playerRankings.put("p"+i, rankings);
			i++;
		}
		numberOfPlayers = playerRankings.size();
		dependencies = Utils.readDependencies(dependenciesFile);
		requirements = Utils.readRequirements(requirementsFile);
//		REQUIREMENT_IDS.addAll(requirements.keySet());
//		Collections.sort(REQUIREMENT_IDS);
	}

//	private void readProblemFromAHP (String ahpVotesFile, String dependenciesFile, String playerWeightsFile, String criteriaWeightsFile){
//		criteria = new TreeMap<String, String[]>();
//		criteriaWeights = new HashMap<String, Double>();
//		playerWeights = new HashMap<String, Map<String, Double>>();
//		playerRankings = new HashMap<String, Map<String, List<String>>> ();
//		requirements = new HashMap<String, String>();
//		
//		
////		Map<String, Map<String, List<String>>> playerRanks = new HashMap<String, Map<String, List<String>>>();
//
//		Set<String> requirementSet = new HashSet<String>();
//		Set<String> criteriaValues = new HashSet<String>();
//
//		List<String[]> matrix = new ArrayList<String[]>();
//		Map<String, List<String[]>> playerVotes = new HashMap<String, List<String[]>>();
//
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(new File(ahpVotesFile)));
//			while (reader.ready()) {
//				String line = reader.readLine();
//				if (line.isEmpty()
//						|| line.startsWith("REQUIREMENT1;REQUIREMENT2;CRITERIA;USER;VOTE;VOTE_TIME")) {
//					continue;
//				} else {
//					// each line has following format
//					// REQUIREMENT1;REQUIREMENT2;CRITERIA;USER;VOTE;VOTE_TIME
//
//					String[] values = line.split(";");
//					String req1 = values[0];
//					String req2 = values[1];
//					String criterion = values[2];
//					String player = values[3];
//					String vote = values[4];
//					String time = values[5];
//
//					requirementSet.add(req1);
//					requirementSet.add(req2);
//					criteriaValues.add(criterion);
//
//					String[] row = new String[4];
//					row[0] = req1;
//					row[1] = req2;
//					row[2] = criterion;
//					row[3] = vote;
//
//					matrix.add(row);
//					
//					if (playerVotes.containsKey(player)){
//						playerVotes.get(player).add(row);
//					}else{
//						List<String[]> m = new ArrayList<String[]>();
//						m.add(row);
//						playerVotes.put(player, m);
//					}
//				}
//			}
//			reader.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// put the requirements from set to list
//		List<String> requirementList = new ArrayList<String>();
////		String[] arr = new String[requirementSet.size()];
//		requirementList.addAll(requirementSet);
//		Collections.sort(requirementList);
//		
//		
//		
//		// for each player, and for each criterion, compute the ranks
//		for (Entry<String, List<String[]>> entry : playerVotes.entrySet()){
//			System.out.println(entry.getKey());
//			
//			Map<String, List<String>> rankCriterion = new HashMap<String, List<String>>();
//			int j = 1;
////			String criterionId;
//			for (String criterion : criteriaValues){
//				System.out.println(criterion);
////				criterionId = "C" + (j++);
//				
//				AHPStructure objAHP = new AHPStructure();
//				
//				// set the criteria
//				objAHP.setCriteria(criterion);
//				
//				// set the alternatives (requirements)
//				objAHP.setOptions(requirementList);
//				
//				// populate the vote matrix
//				for (String[] row : entry.getValue()) {
//					if (row[2].equalsIgnoreCase(criterion)){
//						objAHP.setOptionPreference(row[0], row[1], row[2], Integer.parseInt(row[3]));
//					}
//				}
//
//				Ahp objCalculateRank = new Ahp(objAHP);
//
//				Map<String, Double> result = objCalculateRank.execute();
//
//				LinkedHashMap<String, Double> sortedRanks = (LinkedHashMap<String, Double>) MapUtil.sortByValue(result);
//				
////				Map<String, Integer> rankings = MapUtil.ahpRanksToRanking(result);
//				
//				List<String> ranks = new ArrayList<String>();
//				int i = 0;
////				for (String req : requirementList){
//////					ranks[i++] = rankings.get(req);
////					ranks[i++] = req;
////				}
//				
//				for (Entry<String, Double> ranking : sortedRanks.entrySet()){
//					ranks.add(ranking.getKey());
//				}
//				
//				rankCriterion.put(criterion, ranks);
//				Utils.printArray(ranks);
//				
//				
//				
////				for (Entry<String, Double> e : sortedRanks.entrySet())
////					System.out.println(e.getKey() + ";" + e.getValue());
//			}
//			playerRankings.put(entry.getKey(), rankCriterion);
//
//		}
//		
//		
//		for (String req : requirementList){
//			requirements.put(req, req);
//		}
//		
//		
//		for (String c : criteriaValues){
//			String[] criterionDetail = {c, "min"};
//			String criterionId = c; // "C"+i++;
//			criteria.put(criterionId, criterionDetail);
//		}
//		
//		// weights
//		if (WEIGHT_TYPE == WeightType.INPUT){
//			playerWeights = Utils.readPlayerWeights(playerWeightsFile);
//			criteriaWeights = Utils.readCriteriaWeights(criteriaWeightsFile);
//		}else{
//			for (String c : criteria.keySet()){
//				Map<String, Double> weights = new HashMap<String, Double>();
//				for (String player : playerRankings.keySet()){
//					if (WEIGHT_TYPE == WeightType.RANDOM){
//						weights.put (player, new Double (rnd.nextInt(MAX_WEIGHT) + MIN_WEIGHT));
//					}else{
//						weights.put(player, new Double(MIN_WEIGHT));
//					}
//				}
//				playerWeights.put(criteria.get(c)[0], weights);
//				if (WEIGHT_TYPE == WeightType.RANDOM){
//					criteriaWeights.put(criteria.get(c)[0], new Double(rnd.nextInt(MAX_WEIGHT) + MIN_WEIGHT));
//				}else{
//					criteriaWeights.put(criteria.get(c)[0], new Double(MIN_WEIGHT));
//				}
//			}
//		}
//		// change player weights to probabilities
//		playerWeights = Utils.playerWeightsToProbabilities(playerWeights);
//		criteriaWeights = Utils.criteriaWeightsToProbabilities(criteriaWeights);
//		
//		numberOfPlayers = playerRankings.keySet().size();
//		numberOfRequirements = requirements.size();
//		REQUIREMENT_IDS.addAll(requirementList);
//		
//		dependencies = new HashMap<String, Set<String>> ();
////		dependencies = Utils.readDependencies(dependenciesFile);
//		
//		// export player rankings to file
//		//Utils.exportAnonymizedPlayerRankings(playerRankings, AhpExperimentMain.SUBSYSTEM);
//		//System.exit(0);
//	}
	

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
//		System.err.println(trials + " trails, " + valid);
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
	public static int[] requirementsListToRanking(List<String> solution) {
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


	public Map<String, Map<String, List<String>>> getPlayerRankings() {
		return playerRankings;
	}


	public void setPlayerRankings(Map<String, Map<String, List<String>>> playerRankings) {
		this.playerRankings = playerRankings;
	}


	public Map<String,Set<String>> getDependencies() {
		return dependencies;
	}


	public void setDependencies(Map<String,Set<String>> dependencies) {
		this.dependencies = dependencies;
	}


	public Map<String, Map<String, Double>> getPlayerWeights() {
		return playerWeights;
	}


	public void setPlayerWeights(Map<String, Map<String, Double>> playerWeights) {
		this.playerWeights = playerWeights;
	}


	public Map<String, Double> getCriteriaWeights() {
		return criteriaWeights;
	}


	public void setCriteriaWeights(Map<String, Double> criteriaWeights) {
		this.criteriaWeights = criteriaWeights;
	}


	public SortedMap<String, String[]> getCriteria() {
		return criteria;
	}


	public void setCriteria(SortedMap<String, String[]> criteria) {
		this.criteria = criteria;
	}


	public Map<String, String> getRequirements() {
		return requirements;
	}


	public void setRequirements(Map<String, String> requirements) {
		this.requirements = requirements;
	}
}
