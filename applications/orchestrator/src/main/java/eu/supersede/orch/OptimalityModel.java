package eu.supersede.orch;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.supersede.orch.kb.DOM;

public class OptimalityModel
{
	static class Catalog {
		
		Map<String, ArrayList<Measure>> map = new HashMap<>();

		public ArrayList<Measure> get( String key ) {
			return map.get( key );
		}

		public void put( String key, ArrayList<Measure> list ) {
			map.put( key, list );
		}

		public Iterable<String> keySet() {
			return map.keySet();
		}

		public void remove( String key ) {
			map.remove( key );
		}

		public void add( String key, Measure m ) {
			
			ArrayList<Measure> list = map.get( key );
			
			if( list != null ) {
				list.add( m );
			}
			
		}
	}
	
	Map<String, Measure>				index		= new HashMap<String, Measure>();
	Map<String, ArrayList<Measure>>		catalog		= new HashMap<String, ArrayList<Measure>>();
	
	DOM									registry = new DOM();
	
//	Map<String,Catalog>					catalogs	= new HashMap<>();	
	
	
	
	public <T extends Measure> T addMeasure( T p ) {
		return addMeasure( p, "triggers", "beforeTaskExecution" );
	}
	public <T extends Measure> T addMeasure( T p, String catalogName, String key )
	{
		assert( p.getId() != null );
		
		if( index.get( p.getId() ) != null ) {
			try {
				throw new Exception( "Duplicate entity '" + p.getId() + "'" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
			return p;
		}
		
		index.put( p.getId(), p );
		
		ArrayList<Measure> list = catalog.get( p.getStereotype() );
		
		if( list == null )
		{
			list = new ArrayList<Measure>();
			catalog.put( p.getStereotype(), list );
		}
		
		list.add( p );
		
		registry.create( "/" + catalogName + "/" + key + "/" + p.getId(), p );
		
//		if( getCatalog( key ) == null ) {
//			createCatalog( key );
//		}
//		addToCatalog( p, "trigger", key );
		
		return p;
	}
	
//	public void addToCatalog( Measure m, String catalogName, String key ) {
//		Catalog cat = getCatalog( catalogName );
//		if( cat != null ) {
//			cat.add( key, m );
//		}
//	}
//	public void createCatalog( String key ) {
//		Catalog cat = new Catalog();
//		catalogs.put( key, cat );
//	}
//	public Catalog getCatalog( String key ) {
//		return catalogs.get( key );
//	}
	
	@SuppressWarnings("unchecked")
	public <T extends Measure> T getEntity( String id )
	{
		try
		{
			return (T) index.get( id );
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
//	public void addRelation( Entity[] sources, Entity target,
//			Class<? extends Relation> cls )
//	{
//		try
//		{
//			Relation sat = (Relation) Class.forName( cls.getName() ).newInstance();
//			sat.setTarget( target );
//			
//			for( Entity source : sources )
//				sat.addSource( source );
//			
//			addRelation( sat );
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	public void addRelation( Relation r )
//	{
//		HashMap<String, Relation> rels = relations.get( r.getStereotype() );
//		
//		if( rels == null )
//		{
//			rels = new HashMap<String, Relation>();
//			relations.put( r.getStereotype(), rels );
//		}
//		
//		try
//		{
//			if( rels.get( r.getId() ) != null ) return;
//			
//			rels.put( r.getId(), r );
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	static class EntityIterator<T extends Measure> implements Iterator<T>, Iterable<T>
	{
		ArrayList<Measure>	list;
		Iterator<Measure>	it;
		
		EntityIterator( ArrayList<Measure> list )
		{
			this.list = list;
			this.it = list.iterator();
		}
		
		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T next()
		{
			Measure p = it.next();
			
			return (T) p;
		}
		
		@Override
		public void remove()
		{
			
		}
		
		@Override
		public Iterator<T> iterator()
		{
			return this;
		}
		
	}
	
//	public Iterable<Relation> relations()
//	{
//		class RelationIterator implements Iterable<Relation>, Iterator<Relation>
//		{
//			Iterator<String>	typeIt;
//			Iterator<Relation>	relIt	= null;
//			
//			public RelationIterator( HashMap<String, HashMap<String, Relation>> relations )
//			{
//				typeIt = relations.keySet().iterator();
//			}
//			
//			@Override
//			public Iterator<Relation> iterator()
//			{
//				return this;
//			}
//			
//			@Override
//			public boolean hasNext()
//			{
//				// First iteration
//				if( relIt == null )
//				{
//					relIt = nextIt();
//					
//					if( relIt == null )
//						return false;
//				}
//				
//				if( relIt.hasNext() == true )
//					return true;
//				
//				if( typeIt.hasNext() == false )
//					return false;
//				
//				relIt = nextIt();
//				
//				return relIt.hasNext();
//			}
//			
//			@Override
//			public Relation next()
//			{
//				return relIt.next();
//			}
//			
//			Iterator<Relation> nextIt()
//			{
//				if( typeIt.hasNext() == false )
//					return null;
//				
//				String type = typeIt.next();
//				
//				HashMap<String, Relation> rels = relations.get( type );
//				
//				if( rels == null )
//					return null;
//				
//				return rels.values().iterator();
//			}
//			
//			@Override
//			public void remove()
//			{
//			}
//			
//		}
//		
//		return new RelationIterator( relations );
//	}
	
	public void print( PrintStream out )
	{
		for( String type : entityTypes() )
		{
			for( Measure p : entities( type ) )
			{
				out.println( type + "( " + p.getId() + " )" );
			}
		}
		
//		for( String type : relationTypes() )
//		{
//			for( Relation r : relations( type ) )
//			{
//				out.println( type + "( " + r.target + ", " + r.sources + " ) " );
//			}
//		}
	}
	
	public int getEntityCount( Class<? extends Measure> cls )
	{
		List<Measure> list = catalog.get( cls.getName() );
		
		if( list == null )
			return 0;
		
		if( list.size() > 0 )
			return list.size();
		
		return 0;
	}
	
	public Measure getEntity( Class<? extends Measure> cls, int idx )
	{
		List<Measure> list = catalog.get( cls.getName() );
		
		if( list == null )
			return null;
		
		if( idx >= list.size() )
			return null;
		
		return list.get( idx );
	}
	
	public Measure getEntity( int idx )
	{
		return index.get( idx );
	}
	
	public int getEntityCount()
	{
		return index.size();
	}
	
	public Iterable<Measure> entities()
	{
		return this.index.values();
	}
	
//	public Iterable<String> relationTypes()
//	{
//		return relations.keySet();
//	}
	
	public Iterable<String> entityTypes()
	{
		return catalog.keySet();
	}
	
//	private final ArrayList<Relation> EMPTY_RELATION_LIST = new ArrayList<Relation>();
//	
//	public Iterable<Relation> relations( String type )
//	{
//		HashMap<String,Relation> rels = relations.get( type );
//		
//		if( rels == null ) return EMPTY_RELATION_LIST;
//		
//		return rels.values();
//	}
	
//	public int countRelations()
//	{
//		int ret = 0;
//		
//		for( String rname : relations.keySet() )
//		{
//			HashMap<String,Relation> map = relations.get( rname );
//			ret += map.size();
//		}
//		
//		return ret;
//	}

	public List<Measure> entities( String classname )
	{
		final List<Measure> EMPTY_LIST = new ArrayList<Measure>();
		
		List<Measure> ret = catalog.get( classname );
		
		if( ret == null ) ret = EMPTY_LIST;
		
		return ret;
	}

//	public void addRelation( String[] sources, String target, String stereotype )
//	{
//		Relation r = new Relation( stereotype );
//		
//		for( String s : sources )
//			r.addSource( getEntity( s ) );
//		
//		r.setTarget( getEntity( target ) );
//		
//		addRelation( r );
//	}
//
//	public Relation getRelation( String id ) {
//		for( String type : relations.keySet() ) {
//			Map<String,Relation> map = relations.get( type );
//			if( map.containsKey( id ) )
//				return map.get( id );
//		}
//		return null;
//	}
//
//	public void removeRelation( String rid ) {
//		Relation r = getRelation( rid );
//		if( r == null ) return;
//		while( r.getSourceCount() > 0 )
//			r.removeSource( r.getSources().get( 0 ) );
//		r.setTarget( null );
//		relations.get( r.getStereotype() ).remove( rid );
//	}

	public void removeEntity(String pid) {
		Measure p = getEntity( pid );
		ArrayList<Measure> list = catalog.get( p.getStereotype() );
		if( list != null ) {
			list.remove( p );
			if( list.size() < 1 )
				catalog.remove( p.getStereotype() );
		}
		index.remove( pid );
	}

	public void removeEntity(Measure p1) {
		removeEntity( p1.getId() );
	}

	public ArrayList<Measure> list( String stereotype ) {
		return catalog.get( stereotype );
	}

}
