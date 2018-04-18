package eu.supersede.orch.kb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SortedMap<K, V> 
{
	private final Map<K, V>			map		= new HashMap<K, V>();
	
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
		return map.get( key );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#getKeyCount()
	 */
	
	public int getKeyCount()
	{
		return this.map.size();
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
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#put(K, V)
	 */
	
	public void put( K key, V value )
	{
		V existing = this.map.get( key );
		if( existing == null )
		{
			this.order.add( value );
		}
		this.map.put( key, value );
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#remove(K)
	 */
	
	public void remove( K key )
	{
		V o = get( key );
		
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
	
	public void putAll( SortedMap<K, V> newmap ) {
		for( K key : newmap.keySet() ) {
			this.put( key, newmap.get( key ) );
		}
		for( V value : newmap.order ) {
			order.add( value );
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
		return order.iterator();
	}
	
	/* (non-Javadoc)
	 * @see com.conceptle.nomos.shared.MultiMap#values()
	 */
	
	public Iterable<V> values()
	{
		return order;
	}
}
