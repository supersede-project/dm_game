package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IGAAlgorithm {
	
	List<String> criteria = new ArrayList<>();
	Set<String> requirements = new HashSet<>();
	Map<String,List<String>> dependencies = new HashMap<>();
	Map<String,Double> criteriaWeights = new HashMap<>();
	List<List<String>> rankings = new ArrayList<>();
	
	public void setCriteria( List<String> criteria ) {
		this.criteria.clear();
		this.criteria.addAll( criteria );
		criteriaWeights.clear();
		for( String c : criteria ) {
			criteriaWeights.put( c, 1.0 );
		}
	}
	
	public void setCriteriaWeight( String req, Double w ) {
		criteriaWeights.put( req, w );
	}
	
	public void addRequirement( String req, List<String> deps ) {
		requirements.add( req );
		List<String> list = dependencies.get( req );
		if( list != null ) {
			list.clear();
		}
		else {
			list = new ArrayList<>();
			dependencies.put( req, list );
		}
		list.addAll( deps );
	}
	
	public Map<String,Double> calc() {
		return new HashMap<String,Double>();
	}
	
	public void addRanking( List<String> ranking ) {
		List<String> list = new ArrayList<>();
		list.addAll( ranking );
		this.rankings.add( list );
	}
	
}
