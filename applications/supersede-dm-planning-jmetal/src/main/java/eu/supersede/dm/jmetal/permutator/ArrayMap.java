package eu.supersede.dm.jmetal.permutator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Extension of a map, whose elements can also be retrieved by index
 * 
 * @author albertosiena
 * 
 * @param <K>
 * @param <V>
 */

@SuppressWarnings("serial")
public class ArrayMap<K, V> extends HashMap<K, V> implements Iterable<V>
{
	transient ArrayList<K>	m_list	= new ArrayList<K>();
	
	public ArrayMap()
	{
	}
	
	public ArrayMap( Map<K, V> c )
	{
		putAll( c );
	}
	
	@Override
	public void clear()
	{
		this.m_list.clear();
		super.clear();
	}
	
	public V get( int n )
	{
		return get( key( n ) );
	}
	
	public int indexOf( K key )
	{
		return this.m_list.indexOf( key );
	}
	
	public K key( int n )
	{
		return this.m_list.get( n );
	}
	
	public void printKeys()
	{
		for( int i = 0; i < this.size(); i++ )
		{
			System.out.println( this.key( i ) );
		}
	}
	
	public void printValues()
	{
		for( int i = 0; i < this.size(); i++ )
		{
			System.out.println( this.get( i ) );
		}
	}
	
	@Override
	public V put( K key, V value )
	{
		return put( key, value, this.m_list.size() );
	}
	
	public V put( K key, V value, int index )
	{
		if( this.get( key ) != null )
			return null;
		this.m_list.add( index, key );
		return super.put( key, value );
	}
	
	@Override
	public void putAll( Map<? extends K, ? extends V> t )
	{
		this.m_list.addAll( t.keySet() );
		super.putAll( t );
	}
	
	@Override
	public V remove( Object key )
	{
		this.m_list.remove( key );
		return super.remove( key );
	}
	
	public final ArrayList<K> getKeyList()
	{
		return this.m_list;
	}
	
	class ValueIterator implements Iterator<V>
	{
		int	index	= 0;
		
		@Override
		public boolean hasNext()
		{
			return this.index < size();
		}
		
		@Override
		public V next()
		{
			index++;
			return get( index - 1 );
		}
		
		@Override
		public void remove()
		{
		}
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ValueIterator();
	}
}
