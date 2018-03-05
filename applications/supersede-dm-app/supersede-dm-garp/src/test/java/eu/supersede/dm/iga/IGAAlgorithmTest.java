/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class IGAAlgorithmTest
{
    IGAAlgorithm igaAlgorithm = new IGAAlgorithm();

    @Before
    public void setUp()
    {
        // set criteria
        List<String> criteria = new ArrayList<>();
        String c1 = "c1";
        String c2 = "c2";
        criteria.add(c1);
        criteria.add(c2);

        // set criteria weight
        igaAlgorithm.setCriterionWeight(c1, 1.0);
        igaAlgorithm.setCriterionWeight(c2, 1.0);

        // set requirements
        String r1 = "R1";
        List<String> r1Deps = new ArrayList<>();

        String r2 = "R2";
        String r3 = "R3";

        List<String> r2Deps = new ArrayList<>();
        r2Deps.add(r1);
        List<String> r3Deps = new ArrayList<>();
        r3Deps.add(r1);
        igaAlgorithm.addRequirement(r1, r1Deps);
        igaAlgorithm.addRequirement(r2, r2Deps);
        igaAlgorithm.addRequirement(r3, r3Deps);

        // set player rankings
        String p1 = "P1";
        Map<String, List<String>> rankP1 = new HashMap<>();
        List<String> ranksc1 = new ArrayList<>();
        ranksc1.add(r1);
        ranksc1.add(r2);
        ranksc1.add(r3);
        List<String> ranksc2 = new ArrayList<>();
        ranksc2.add(r1);
        ranksc2.add(r3);
        ranksc2.add(r2);
        rankP1.put(c1, ranksc1);
        rankP1.put(c2, ranksc2);
        igaAlgorithm.addRanking(p1, rankP1);

        String p2 = "P2";
        Map<String, List<String>> rankP2 = new HashMap<>();
        List<String> ranksp2c1 = new ArrayList<>();
        ranksp2c1.add(r3);
        ranksp2c1.add(r2);
        ranksp2c1.add(r1);
        List<String> ranksp2c2 = new ArrayList<>();
        ranksp2c2.add(r2);
        ranksp2c2.add(r1);
        ranksp2c2.add(r3);
        rankP2.put(c1, ranksp2c1);
        rankP2.put(c2, ranksp2c2);
        igaAlgorithm.addRanking(p2, rankP2);

        // set player weights
        Map<String, Double> weightsC1 = new HashMap<>();
        weightsC1.put(p1, 1.0);
        weightsC1.put(p2, 1.0);
        igaAlgorithm.setPlayerWeights(c1, weightsC1);

        Map<String, Double> weightsC2 = new HashMap<>();
        weightsC2.put(p1, 1.0);
        weightsC2.put(p2, 1.0);
        igaAlgorithm.setPlayerWeights(c2, weightsC2);

    }

    @Test
    public void testCalc()
    {
        List<GARequirementsRanking> rankings = igaAlgorithm.calc();

        for (GARequirementsRanking ranking : rankings)
        {
            System.out.println("Ranking: " + ranking.getRequirements());

            for (Entry<String, Double> entry : ranking.getObjectiveValues().entrySet())
            {
                System.out.println("[" + entry.getKey() + " = " + entry.getValue() + "]");
            }
        }
    }
    
    @Test 
    public void testCalcSinglePlayer() {
    	igaAlgorithm.rankings.remove("P2");
    	List<GARequirementsRanking> rankings = igaAlgorithm.calc();
    	for (GARequirementsRanking ranking : rankings)
        {
            System.out.println("Ranking: " + ranking.getRequirements());

            for (Entry<String, Double> entry : ranking.getObjectiveValues().entrySet())
            {
                System.out.println("[" + entry.getKey() + " = " + entry.getValue() + "]");
            }
        }
    }
}