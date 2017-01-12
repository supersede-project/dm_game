package eu.supersede.gr.model;
//package eu.supersede.dm.ga.db;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import eu.supersede.dm.ga.IGADataview;
//import eu.supersede.dm.ga.data.GAGameDetails;
//import eu.supersede.dm.ga.data.GAGameSummary;
//import eu.supersede.dm.ga.jpa.AttributesJpa;
//import eu.supersede.dm.ga.jpa.EntitiesJpa;
//import eu.supersede.dm.ga.jpa.GAGameDetailsJpa;
//import eu.supersede.dm.ga.jpa.GAGameParticipationJpa;
//import eu.supersede.dm.ga.jpa.GAGameSummaryJpa;
//
//public class PostgresDataview implements IGADataview {
//	
//	public interface DataWrapper<T,U> {
//		public T get( U value );
//	}
//	
//	public static class ListWrapper<T,U> implements List<T> {
//		
//		List<U>				list;
//		DataWrapper<T,U>	wrapper;
//		
//		public ListWrapper( List<U> list, DataWrapper<T,U> wrapper ) {
//			this.list = list;
//			this.wrapper = wrapper;
//		}
//		
//		@Override
//		public Iterator<T> iterator() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int size() {
//			return list.size();
//		}
//
//		@Override
//		public boolean isEmpty() {
//			return list.isEmpty();
//		}
//
//		@Override
//		public boolean contains(Object o) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public Object[] toArray() {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public <S> S[] toArray(S[] a) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean add(T e) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean remove(Object o) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean containsAll(Collection<?> c) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean addAll(Collection<? extends T> c) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean addAll(int index, Collection<? extends T> c) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean removeAll(Collection<?> c) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public boolean retainAll(Collection<?> c) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public void clear() {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public T get(int index) {
//			return this.wrapper.get( list.get( index ) );
//		}
//
//		@Override
//		public T set(int index, T element) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public void add(int index, T element) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public T remove(int index) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public int indexOf(Object o) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public int lastIndexOf(Object o) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public ListIterator<T> listIterator() {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public ListIterator<T> listIterator(int index) {
//			throw new RuntimeException( "Unsupported" );
//		}
//
//		@Override
//		public List<T> subList(int fromIndex, int toIndex) {
//			throw new RuntimeException( "Unsupported" );
//		}}
//	
//	@Autowired
//    private GAGameSummaryJpa		gameSummaryTable;
//    private GAGameDetailsJpa		gameDetailsTable;
//    private GAGameParticipationJpa	gameParticipation;
//	
//    private EntitiesJpa				entities;
//    private AttributesJpa			attributes;
//    
//	@Override
//	public GAGameDetails create(GAGameSummary game) {
//		
//        GAGameDetails gi = new GAGameDetails();
//        gi.setGame( game );
//        gameDetailsTable.save( new HGAGameDetails( gi ) );
//        gameSummaryTable.save( new HGAGameSummary( game ) );
//        
//        HEntity entity = null;
//        
//        
//        
//        entity = new HEntity();
//        entity.setClsName( "GameSummary" );
//        entities.save( entity );
//        
//        setAttr( entity, "status", game.getStatus() );
//        setAttr( entity, "date", game.getDate() );
//        setAttr( entity, "orwner", "" + game.getOwner() );
//        
//        
//        
//        entity = new HEntity();
//        entity.setClsName( "GameDetail");
//        entities.save( entity );
//        
//        return gi;
//	}
//	
//	private void setAttr( HEntity entity, String name, String value ) {
//        HAttribute attr = new HAttribute();
//        attr.setEntityId( entity.getId() );
//        attr.setName( name );
//        attr.setValue( value );
//        attributes.save( attr );
//	}
//	
//	@Override
//	public void create(GAGameSummary game, List<String> criteria, List<Long> requirements, List<Long> participants) {
//		
//		GAGameDetails gi = new GAGameDetails();
//		
//		gi.setGame( game );
//		gi.setCriteria( criteria );
//		gi.setRequirements( requirements );
//		gi.setParticipants( participants );
//		
//		HGAGameDetails hgi = new HGAGameDetails( gi );
//		HGAGameSummary hgs = new HGAGameSummary( game );
//		
//		gameDetailsTable.save( hgi );
//        gameSummaryTable.save( hgs );
//		
//	}
//
//	@Override
//	public List<GAGameSummary> getOwnedGames(Long owner) {
//		return gameSummaryTable.findByOwner( owner );
//	}
//
//	@Override
//	public List<GAGameSummary> getActiveGames(Long userId) {
//		
//		
//		
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setRanking(Long gameId, Long userId, Map<String, List<Long>> reqs) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public List<Long> getRankingsCriterion(Long gameId, Long userId, String criterion) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, List<Long>> getRanking(Long gameId, Long userId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GAGameDetails getGameInfo(Long gameId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Long> getParticipants(GAGameSummary game) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Long> getParticipants(Long gameId) {
//		return gameParticipation.findParticipants( gameId );
//	}
//
//	@Override
//	public List<String> getCriteria(GAGameSummary game) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getCriteria(long gameId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Long> getRequirements(Long gameId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, List<Long>> getRequirements(Long gameId, Long userId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
