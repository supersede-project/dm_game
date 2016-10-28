package eu.supersede.dm.jmetal.permutator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Permutation<T,V>
{
	ArrayList<T>		variables = new ArrayList<T>();
	
	Map<T,V>			values = new HashMap<T,V>();
	
	
	public static <T,V> Permutation<T,V> forTable( PermutationTable<T,V> t ) {
		return new Permutation<T,V>( t.variables );
	}
	
	public static <T,V> Permutation<T,V> forTable( PermutationTable<T,V> t, V[] vals ) {
		Permutation<T,V> p = new Permutation<T,V>( t.variables );
		p.setValues( vals );
		return p;
	}
	
	public Permutation() {}
	
	public Permutation( T[] v ) {
		for( T s : v ) {
			variables.add( s );
		}
	}
	
	public Permutation( ArrayList<T> v ) {
		for( T s : v ) {
			variables.add( s );
		}
	}
	
	void setValues( V[] vals ) {
		values.clear();
		for( int i = 0; i < variables.size(); i++ )
			values.put( variables.get( i ), vals[i] );
	}
	
	public V getValue( T variable ) {
		return values.get( variable );
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		String sep = "";
		for( int i = 0; i < variables.size(); i++ )
		{
			s.append( sep + variables.get( i ) + "=" + values.get( i ) );
			sep = ", ";
		}
		return s.toString();
	}
	
	public T variable( int n ) {
		return variables.get( n );
	}
	
	public V value( int n ) {
		return values.get( n );
	}
	
	public int getVariableCount() {
		return variables.size();
	}

	public Collection<V> getValues() {
		return this.values.values();
	}
	
	public Collection<T> variables() {
		return variables;
	}

	public void print(PrintStream out) {
		for( T var : variables() ) {
			out.print( var + "=" + getValue( var ) + ";" );
		}
		System.out.println();
	}
	
	public final Map<T,V> asMap() {
		return this.values;
	}
}