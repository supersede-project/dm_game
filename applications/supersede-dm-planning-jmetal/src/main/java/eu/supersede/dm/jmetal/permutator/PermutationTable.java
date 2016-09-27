package eu.supersede.dm.jmetal.permutator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PermutationTable<T,V> implements Iterable<Permutation<T,V>>
{
	ArrayList<T>				variables = new ArrayList<T>();
	MultiMap<T,List<V>>			values = new MultiMap<T,List<V>>();
	
	public int getVariableCount() {
		return variables.size();
	}
	
	public int getValueCount( T variable ) {
		return values.get( variable ).size();
	}
	
	public int[] getBoundaries() {
		int[] imax = new int[variables.size()];
		for( int i = 0; i < variables.size(); i++ ) {
			imax[i] = values.get( variables.get( i ) ).size();
		}
		return imax;
	}
	
	public void setVariables( T[] variables ) {
		for( T s : variables )
			addVariable( s );
	}
	
	public void addVariable( T var ) {
		this.variables.add( var );
		this.values.put( var, new ArrayList<V>() );
	}
	
	public void setValues( T variable, V[] values ) {
		ArrayList<V> list = new ArrayList<V>();
		for( V v : values ) {
			list.add( v );
		}
		setValues( variable, list );
	}
	
	public void setValues( T variable, List<V> list ) {
		if( values.get( variable ) == null )
			addVariable( variable );
		this.values.remove( variable );
		this.values.put( variable, list );
	}
	
	public void addValue( T variable, V value ) {
		if( values.get( variable ) == null )
			addVariable( variable );
		this.values.get( variable ).add( value );
	}
	
	@Override
	public Iterator<Permutation<T,V>> iterator() {
		return new Permutator<T,V>( this );
	}
	
//	public Iterable<Permutation<T,V>> search( String[] query, int type ) {
//		return new Permutator<T,V>( this, query, type );
//	}

	public V getValue( int i, int index ) {
		return values.get( variables.get( i ) ).get( index );
	}
	
	public Iterable<T> variables() {
		return variables;
	}

	public T getVariable( int i ) {
		return variables.get( i );
	}
	
}