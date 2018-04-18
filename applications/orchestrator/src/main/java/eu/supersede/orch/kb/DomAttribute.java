package eu.supersede.orch.kb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DomAttribute {
	
	static class Pair {
		String name;
		ArrayList<String> values = new ArrayList<String>();
		
		public Pair( String name, String value ) {
			this.name = name;
			this.values.add( value );
		}
		
		private Pair() {}
		
		public String getValue() {
			String ret = "";
			String sep = "";
			for( String v : values )
			{
				ret += sep + v;
				sep = ";";
			}
			return ret;
		}

		public String getName() {
			return this.name;
		}
		
		public Pair clone() {
			Pair other = new Pair();
			other.name = name;
			for( String val : values ) {
				other.values.add( val );
			}
			return other;
		}
		
	}
	
	ArrayList<Pair> pairs = new ArrayList<Pair>();
	Map<String,Pair> index = new HashMap<String,Pair>();
	
	public DomAttribute( String value )
	{
		for( String part : value.split( "[;]" ) )
		{
			int p = value.indexOf( ":" );
			if( p == -1 )
			{
				Pair pair = new Pair( "", part );
				pairs.add( pair );
			}
			else
			{
				Pair pair = new Pair( part.substring( 0, p ), part.substring( p +1 ) );
				pairs.add( pair );
				index.put( pair.name, pair );
			}
		}
	}
	
	public boolean isSingle() {
		return index.size() == 0;
	}
	
	public DomAttribute clone() {
		if( isSingle() ) {
			return new DomAttribute( getValue() );
		}
		DomAttribute attr = new DomAttribute( "" );
		for( Pair pair : pairs ) {
			Pair clone = pair.clone();
			attr.pairs.add( clone );
			if( index.containsKey( pair.name ) ) {
				attr.index.put( clone.name, clone );
			}
		}
		return attr;
	}
	
	public Collection<Pair> values() {
		return this.pairs;
	}
	
	public void addField( String key, String value ) {
		Pair pair = new Pair( key, value );
		pairs.add( pair );
		index.put( pair.getName(), pair );
	}
	
	public String getField( String field ) {
		Pair pair = index.get( field );
		if( pair == null ) return null;
		return pair.values.get( 0 );
	}
	
	public String toString() {
		if( index.size() > 0 ) {
			String ret = "";
			for( String k : index.keySet() )
//			for( Pair p : pairs )
			{
				Pair p = index.get( k );
				ret += p.name;
				if( p.values.size() > 0 )
					ret += ":" + p.getValue() + ";";
			}
			return ret;
		}
		else
		{
			return pairs.get( 0 ).values.get( 0 );
		}
		
	}

	public String getValue()
	{
		return toString();
	}
}
