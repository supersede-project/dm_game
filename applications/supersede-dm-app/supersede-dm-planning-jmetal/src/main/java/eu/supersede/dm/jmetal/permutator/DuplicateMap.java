package eu.supersede.dm.jmetal.permutator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DuplicateMap<K,V>
{
	private Map<K,ArrayList<V>> map = new TreeMap<K,ArrayList<V>>();
	private ArrayList<V> order = new ArrayList<V>();

	public List<V> getList( Object key )
	{
		return map.get( key );
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public boolean containsKey( K key )
	{
		return map.containsKey( key );
	}
	public boolean containsValue( V value )
	{
		return order.contains( value );
	}

	public void put( K key, V value )
	{
		List<V> existing = map.get( key );
		if ( existing == null )
		{
			ArrayList<V> list = new ArrayList<V>();
			list.add( value );
			map.put( key, list );
		}
		else
		{
			existing.add( value );
		}
		order.add( value );
	}
	public void add( K key, V value )
	{
		put( key, value );
	}

	public V get( K key, int index )
	{
		List<V> list = map.get( key );

		if( list == null ) return null;

		if( index > (list.size() -1) ) return null;

		return list.get( index );
	}

	public V get( K key )
	{
		return get( key, 0 );
	}
	public V get( int index )
	{
		return order.get( index );
	}

	public ArrayList<V> list( K key )
	{
		ArrayList<V> list = map.get( key );
		if( list != null ) return list;
		return new ArrayList<V>();
	}

	public Set<K> keySet()
	{
		return map.keySet();
	}
	public ArrayList<V> valueSet()
	{
		return order;
	}
	public int count( K key )
	{
		List<V> list = map.get( key );

		if( list == null ) return 0;

		return list.size();
	}
	public int keyCount()
	{
		return map.size();
	}
	public int size()
	{
		return order.size();
	}
	public void clear()
	{
		map.clear();
		order.clear();
	}

	public void remove( K key )
	{
		V o = get( key );

		List<V> list = map.get( key );

		if( list == null ) return;

		list.clear();

		order.remove( o );
	}

	public void remove( K key, int n )
	{
		V o = get( key );

		List<V> list = map.get( key );

		if( list == null ) return;

		list.remove( n );

		order.remove( o );
	}
}
