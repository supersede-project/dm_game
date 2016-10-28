package eu.supersede.dm.jmetal.permutator;

import java.util.Iterator;

public class Permutator<T,V> implements Iterator<Permutation<T,V>>, Iterable<Permutation<T,V>>
{
	PermutationCounter	c;
	
	Permutation<T,V>			p;
	
	PermutationTable<T,V>	table;
	
	Filter<T,V>			filter;
	
	public interface Filter<T,V>
	{
		boolean match( Permutation<T,V> p );
	}
	
	static class NoFilter<T,V> implements Filter<T,V>
	{
		
		@Override
		public boolean match( Permutation<T,V> p )
		{
			return true;
		}
		
	}
	
//	static class AndFilter<T,V> implements Filter<T,V> {
//		ArrayList<V>	values	= new ArrayList<V>();
//		
//		AndFilter( V[] values ) {
//			for( V v : values )
//				this.values.add( v );
//		}
//		
//		@Override
//		public boolean match( Permutation<T,V> p ) {
//			// At least one does not match
//			for( V val : p.values )
//				if( values.contains( val ) == false )
//					return false;
//			
//			return true;
//		}
//		
//	}
//	
//	static class OrFilter<T,V> implements Filter<T,V> {
//		private final V[]	values;
//		
//		OrFilter( V[] values ) {
//			this.values = values;
//		}
//		
//		@Override
//		public boolean match( Permutation<T,V> p ) {
//			for( int i = 0; i < values.length; i++ ) {
//				// At least one match
//				for( V val : p.values )
//					if( val.equals( values[i] ) )
//						return true;
//			}
//			
//			// No match
//			return false;
//		}
//		
//	}
	
	public Permutator( PermutationTable<T,V> pt )
	{
		this.table = pt;
		c = new PermutationCounter( pt.getVariableCount(), pt.getBoundaries() );
		p = new Permutation<T,V>( pt.variables );
		filter = new NoFilter<T,V>();
	}
	
//	@SuppressWarnings("unchecked")
//	public Permutator( PermutationTable<T,V> pt, String[] query, int type )
//	{
//		this.table = pt;
//		c = new PermutationCounter( pt.getVariableCount(), pt.getBoundaries() );
//		p = new Permutation<T,V>( pt.variables );
//		
//		filter = (Filter<T,V>) createFilter( query, type );
//		
//		while( (filter.match( getPermutation( c ) ) == false) ) {
//			c.increase();
//		}
//	}
	
	@Override
	public boolean hasNext()
	{
		return c.isMax() == false;
	}
	
	Permutation<T,V> getPermutation( PermutationCounter c )
	{
		int[] indexes = c.indexes();
		
		@SuppressWarnings("unchecked")
		V[] ret = (V[])new Object[indexes.length];
		
		for( int i = 0; i < indexes.length; i++ )
		{
			ret[i] = table.getValue( i, indexes[i] );
		}
		
		Permutation<T,V> perm = new Permutation<T,V>( this.table.variables );
		perm.setValues( ret );
		
		return perm;
	}
	
	@Override
	public Permutation<T,V> next() {
		
		int[] indexes = c.indexes();
		
		@SuppressWarnings("unchecked")
		V[] ret = (V[])new Object[indexes.length];
		
		for( int i = 0; i < indexes.length; i++ ) {
			ret[i] = table.getValue( i, indexes[i] );
		}
		
		p.setValues( ret );
		
		do {
			c.increase();
		} while( (filter.match( getPermutation( c ) ) == false) && (c.isMax() == false) );
		
		return p;
	}
	
	@Override
	public void remove() {
		throw new RuntimeException( "Unsupprted" );
	}
	
	@Override
	public Iterator<Permutation<T,V>> iterator() {
		return this;
	}
	
//	public static <T,V> Filter<T,V> createFilter( V[] values, int type ) {
//		return (type == 0 ? new AndFilter<T,V>( values ) : new OrFilter<T,V>( values ));
//	}
	
	public void setFilter( Filter<T,V> filter ) {
		this.filter = filter;
	}
	
	public PermutationTable<T,V> getTable() {
		return this.table;
	}
	
}