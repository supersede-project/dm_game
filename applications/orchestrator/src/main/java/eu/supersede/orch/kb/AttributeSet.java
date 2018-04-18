package eu.supersede.orch.kb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AttributeSet implements Iterable<String>
{

	private final Map<String, DomAttribute>			attributes	= new HashMap<String, DomAttribute>();


	public AttributeSet clone() {
		AttributeSet set = new AttributeSet();
		for( String key : attributes.keySet() ) {
			set.put( key, attributes.get( key ).clone() );
		}
		return set;
	}

	public DomAttribute get( String attribute ) {
		return attributes.get( attribute );
	}

	public DomAttribute get( String attribute, String def ) {
		DomAttribute attr = get( attribute );

		if( attr == null )
			return new DomAttribute( def );

		return attr;
	}

	public void put( String key, String value ) {
		attributes.put( key, new DomAttribute( value ) );
	}

	public void put( String key, DomAttribute attr ) {
		attributes.put( key, attr );
	}

	@Override
	public Iterator<String> iterator() {
		return attributes.keySet().iterator();
	}

	public Iterable<String> keySet() {
		return this;
	}

	public int size() {
		return attributes.size();
	}

	public void remove(String key) {
		attributes.remove( key );
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for( String s : attributes.keySet() ) {
			b.append( s + "=" + get(s,"") + ";" );
		}
		return b.toString();}

}
