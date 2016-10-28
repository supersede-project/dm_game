package eu.supersede.dm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DMPrioritization {
	
	Set<DMOutputItem> items = new HashSet<>();
	
	Map<Double,String> ranking = new TreeMap<>();
	
	
	public void addItem( DMOutputItem item ) {
		this.items.add( item );
	}
	
	public void setRanking( DMOutputItem item, double r ) {
		ranking.put( r, item.id );
	}
	
	
}
