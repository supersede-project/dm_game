package eu.supersede.dm.jmetal.permutator;

import java.util.Iterator;

public class PermutationIterator<T,V> implements Iterator<Permutation<T,V>>, Iterable<Permutation<T,V>> {
	
	PermutationTable<T,V>		table;
	
	Filter<T,V>					filter;
	
	Iterator<Permutation<T,V>>	it;
	
	Permutation<T,V>			current = null;
	
	
	public interface Filter<T,V> {
		boolean match( Permutation<T,V> p );
	}
	
	static class NoFilter<T,V> implements Filter<T,V> {
		@Override
		public boolean match( Permutation<T,V> p ) {
			return true;
		}
	}
	
	public PermutationIterator( PermutationTable<T,V> pt )
	{
		this.table = pt;
		filter = new NoFilter<T,V>();
		it = table.iterator();
		current = findNext();
	}
	
	private Permutation<T,V> findNext() {
		if( it == null )
			return null;
		while( it.hasNext() ) {
			Permutation<T,V> p = it.next();
			if( filter.match( p ) ) {
				return p;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasNext() {
		return current != null;
	}
	
	@Override
	public Permutation<T,V> next() {
		
		Permutation<T,V> ret = current;
		
		current = findNext();
		
		return ret;
	}
	
	@Override
	public void remove() {
		throw new RuntimeException( "Unsupprted" );
	}
	
	@Override
	public Iterator<Permutation<T,V>> iterator() {
		return this;
	}
	
	public void setFilter( Filter<T,V> filter ) {
		this.filter = filter;
	}
}