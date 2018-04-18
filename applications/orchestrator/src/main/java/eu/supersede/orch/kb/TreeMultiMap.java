package eu.supersede.orch.kb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeMultiMap<K, V> 
{
	private final Map<K, List<V>>	map		= new TreeMap<K, List<V>>();
	
	private final ArrayList<V>		order	= new ArrayList<V>();
	
	public void add( K key, V value )
	{
		put( key, value );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#clear()
	 */
	
	public void clear()
	{
		this.map.clear();
		this.order.clear();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#containsKey(K)
	 */
	
	public boolean containsKey( K key )
	{
		return this.map.containsKey( key );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#containsValue(V)
	 */
	
	public boolean containsValue( V value )
	{
		return this.order.contains( value );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#count(K)
	 */
	
	public int count( K key )
	{
		List<V> list = this.map.get( key );
		
		if( list == null )
		{
			return 0;
		}
		
		return list.size();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#get(int)
	 */
	
	public V get( int index )
	{
		return order.get( index );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#get(K)
	 */
	
	public V get( K key )
	{
		return get( key, 0 );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#get(K, int)
	 */
	
	public V get( K key, int index )
	{
		List<V> list = this.map.get( key );
		
		if( list == null )
		{
			return null;
		}
		
		if( index > (list.size() - 1) )
		{
			return null;
		}
		
		return list.get( index );
	}
	
	// public K getKey( int n )
	// {
	// return this.order.get( n );
	// }
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#getKeyCount()
	 */
	
	public int getKeyCount()
	{
		return this.map.size();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#getList(K)
	 */
	
	public java.util.List<V> getList( K key )
	{
		return this.map.get( key );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#isEmpty()
	 */
	
	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}
	
	public int keyCount()
	{
		return this.map.size();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#keySet()
	 */
	
	public Set<K> keySet()
	{
		return this.map.keySet();
	}
	
	public List<V> list( K key )
	{
		List<V> list = this.map.get( key );
		if( list != null )
		{
			return list;
		}
		return new ArrayList<V>();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#put(K, V)
	 */
	
	public void put( K key, V value )
	{
		List<V> existing = this.map.get( key );
		if( existing == null )
		{
			List<V> list = new ArrayList<V>();
			list.add( value );
			this.map.put( key, list );
		} else
		{
			existing.add( value );
		}
		this.order.add( value );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#remove(K)
	 */
	
	public void remove( K key )
	{
		V o = get( key );
		
		List<V> list = this.map.get( key );
		
		if( list == null )
		{
			return;
		}
		
		list.clear();
		
		this.order.remove( o );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#remove(K, int)
	 */
	
	public void remove( K key, int n )
	{
		V o = get( key );
		
		List<V> list = this.map.get( key );
		
		if( list == null )
		{
			return;
		}
		
		list.remove( n );
		
		this.order.remove( o );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#size()
	 */
	
	public int size()
	{
		return this.order.size();
	}
	
	// public ArrayList<V> valueSet()
	// {
	// return this.order;
	// }
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#putAll(com.conceptle.nomos.shared.MultiMap)
	 */
	
	public void putAll( TreeMultiMap<K, V> newmap )
	{
		for( K key : newmap.keySet() )
		{
			for( int v = 0; v < newmap.count( key ); v++ )
			{
				V value = newmap.get( key, v );
				
				this.put( key, value );
			}
		}
	}
	
	class ValueIterator implements Iterator<V>
	{
		int	x	= 0, y = 0;
		
		
		public boolean hasNext()
		{
			return x < order.size();
		}
		
		
		public V next()
		{
			List<V> list = map.get( order.get( x ) );
			
			V v = list.get( y );
			
			y++;
			
			if( y >= list.size() )
			{
				y = 0;
				x++;
			}
			
			return v;
		}
		
		
		public void remove()
		{
			
		}
		
	}
	
	public int indexOf( V value )
	{
		return order.indexOf( value );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#iterator()
	 */
	
	public Iterator<V> iterator()
	{
		return new ValueIterator();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#values()
	 */
	
	public Iterable<V> values()
	{
		return order;
	}
}
