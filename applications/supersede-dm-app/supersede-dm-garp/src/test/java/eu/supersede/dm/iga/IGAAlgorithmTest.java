package eu.supersede.dm.iga;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class IGAAlgorithmTest {

	@Test
	public void testCalc() {
		IGAAlgorithm igaAlgorithm = new IGAAlgorithm();
		
		// set criteria
		List<String> criteria = new ArrayList<String>();
		String c1 = "c1";
		String c2 = "c2";
		criteria.add(c1);
		criteria.add(c2);
		igaAlgorithm.setCriteria(criteria);
		
		// set criteria weight
		igaAlgorithm.setCriteriaWeight(c1, 1.0);
		igaAlgorithm.setCriteriaWeight(c2, 1.0);
		
		// set requirements
		String r1 = "R1";
		List<String> r1Deps = new ArrayList<String>();
		
		String r2 = "R2";
		String r3 = "R3";
		
		List<String> r2Deps = new ArrayList<String>();
		r2Deps.add(r1);
		List<String> r3Deps = new ArrayList<String>();
		r3Deps.add(r1);
		igaAlgorithm.addRequirement(r1, r1Deps);
		igaAlgorithm.addRequirement(r2, r2Deps);
		igaAlgorithm.addRequirement(r3, r3Deps);
	
		// set player rankings
		String p1 = "P1";
		Map<String, List<String>> rankP1 = new HashMap<String, List<String>>();
		List<String> ranksc1 = new ArrayList<String>();
		ranksc1.add(r1); ranksc1.add(r2); ranksc1.add(r3);
		List<String> ranksc2 = new ArrayList<String>();
		ranksc2.add(r1); ranksc2.add(r2); ranksc2.add(r3);
		rankP1.put(c1, ranksc1); rankP1.put(c2, ranksc2);
		igaAlgorithm.addRanking(p1, rankP1);
		
		String p2 = "P2";
		Map<String, List<String>> rankP2 = new HashMap<String, List<String>>();
		List<String> ranksp2c1 = new ArrayList<String>();
		ranksp2c1.add(r1); ranksp2c1.add(r2); ranksp2c1.add(r3);
		List<String> ranksp2c2 = new ArrayList<String>();
		ranksp2c2.add(r1); ranksp2c2.add(r2); ranksp2c2.add(r3);
		rankP2.put(c1, ranksp2c1); rankP2.put(c2, ranksp2c2);
		igaAlgorithm.addRanking(p2, rankP2);
		
		// set player weights
		Map<String, Double> weightsC1 = new HashMap<String, Double>();
		weightsC1.put(p1, 1.0);
		weightsC1.put(p2, 1.0);
		igaAlgorithm.addPlayerRanking(c1, weightsC1);
		
		Map<String, Double> weightsC2 = new HashMap<String, Double>();
		weightsC2.put(p1, 1.0);
		weightsC2.put(p2, 1.0);
		igaAlgorithm.addPlayerRanking(c2, weightsC2);
		
		
		// invoke the algorithm
		List<Map<String, Double>> rankings = igaAlgorithm.calc();
		for (Map<String, Double> ranking : rankings){
			for (Entry<String, Double> entry : ranking.entrySet()){
				System.out.print(entry.getKey() + ";");
			}
			System.out.println();
		}
	}

}
