package eu.supersede.dm.iga.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import eu.supersede.dm.iga.utils.Utils;

public class UtilTest {

	String inputDir = "resources/input/PRESTO/SceneSelection/";
	int numRequirements = 5;
	
	@Test
	public void testReadDependencies() {
		String deps = inputDir + "dependencies";
		Map<String, Set<String>> dependencies = Utils.readDependencies(deps);
		assertTrue(dependencies.size() == 0);
//		assertTrue(dependencies.get("R2").size() == 2);
//		assertTrue(dependencies.get("R3").size() == 1);
//		assertTrue(dependencies.get("R4").size() == 2);
	}

	@Test
	public void testReadPlayerRankings(){
		String ranks = inputDir + "ranking_p1.csv";
		;
		Map<String, List<String>> playerRankings = Utils.readPlayerRankings(ranks);
		assertTrue(playerRankings.size() == 2);
		assertTrue(playerRankings.get("c1").size() == numRequirements);
		assertTrue(playerRankings.get("c2").size() == numRequirements);
	}
	
	@Test
	public void testReadPlayerWeights(){
		String weights = inputDir + "weights_player.csv";
		Map<String, Map<String, Double>> playerWeights = Utils.readPlayerWeights(weights);
		assertTrue(playerWeights.size() == 2);
		assertTrue(playerWeights.get("c1").get("p1") != null);
	}
	
	
	@Test
	public void testReadCriteriaWeights(){
		String weights = inputDir + "weights_criteria.csv";
		int numCriteria = 2;
		Map<String, Double> criteriaWeights = Utils.readCriteriaWeights(weights);
		assertTrue(criteriaWeights.size() == numCriteria);
		assertTrue(criteriaWeights.get("c1") == 1);
	}
	
	@Test
	public void testReadCriteria(){
		String criteriaFile = inputDir + "criteria.csv";
		Map<String, String[]> criteria = Utils.readCriteria(criteriaFile);
		assertTrue(criteria.size() == 2);
	}
	
	@Test
	public void testReadRequirements(){
		String requirementsFile = inputDir + "requirements.csv";
		Map<String, String> requirements = Utils.readRequirements(requirementsFile);
		assertTrue(requirements.size() == numRequirements);
		assertTrue(requirements.containsKey("R1"));
		assertTrue(requirements.containsKey("R5"));
	}
	
//	@Test
//	public void testReadAHPRanks (){
//		String votesFile = "resources/input/ahp/VOTES/SceneSelection.csv";
//		
//		Map<String, String[]> criteria = new HashMap<String, String[]> ();
//		Map<String, Double> criteriaWeight = new HashMap<String, Double>();
//		Map<String, double[]> playerWeight = new HashMap<String, double[]>();
//		Map<String, Map<String, int[]>> ahpRanks = Utils.readAHPRanks(votesFile, criteria, criteriaWeight, playerWeight);
//		for (Entry<String, Map<String,int[]>> entry : ahpRanks.entrySet()){
//			System.out.println(entry.getKey());
//			for (Entry<String, int[]> e : entry.getValue().entrySet()){
//				System.out.print(e.getKey());
//				for (int r : e.getValue()){
//					System.out.print(r + ";");
//				}
//				System.out.println();
//			}
//		}
//	}
}
