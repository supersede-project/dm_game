package eu.supersede.dm.jmetal;

import java.util.Map;

import eu.supersede.dm.jmetal.permutator.Permutation;

public interface ScoreCalculator {
	
	double calculate( Permutation<String,String> p, Map<String,String> constraints );
	
}