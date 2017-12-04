package eu.supersede.dm.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FilesystemRankingAssistant implements RankingAssistant {
	
	public static class FileLineReader implements Iterable<String> {
		
		public class LineIterator implements Iterator<String> {
			
			BufferedReader br;
			String line = null;
			
			public LineIterator() {
				try {
					br = new BufferedReader(new InputStreamReader( is, "UTF-8" ) );
					line = br.readLine();
				} catch (Exception e) {
					e.printStackTrace();
					line = null;
				}
			}
			
			@Override
			public boolean hasNext() {
				return line != null;
			}

			@Override
			public String next() {
				String ret = line;
				try {
					line = br.readLine();
					if( line == null ) {
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return ret;
			}
			
		}

		private InputStream is;
		
		public FileLineReader( InputStream is ) {
			this.is = is;
		}

		@Override
		public Iterator<String> iterator() {
			return new LineIterator();
		}
		
	}
	
	private GAPersistentDB db;
	private Long gameId;
	
	private Map<String,String> options = new HashMap<>();
	
	private List<Long> aiParticipants = new ArrayList<>();
//	private Map<Long, List<Long>> aiRanking = new HashMap<>();
	private List<Long> ranking = new ArrayList<>();

	public FilesystemRankingAssistant( GAPersistentDB db, Long gameId ) {
		this.db = db;
		this.gameId = gameId;
		GAGameDetails info = db.getGameInfo( gameId );
		loadData( info.getGame().getName() );
	}

	public void loadData( String gameName ) {
		
		try( InputStream is = this.getClass().getClassLoader().getResourceAsStream( "static/garp/res/rankings/" + gameName + "/info.txt" ) ) {
			for( String line : new FileLineReader( is ) ) {
				String[] parts = line.split( "[=]" );
				if( parts == null ) continue;
				if( parts.length < 2 ) continue;
				options.put( parts[0].trim(), parts[1].trim() );
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
		aiParticipants.add( Long.parseLong( getOption( "userId", "-1" ) ) );
		
		try( InputStream is = this.getClass().getClassLoader().getResourceAsStream( "static/garp/res/rankings/" + gameName + "/ranking.txt" ) ) {
			List<Long> ranking = new ArrayList<>();
			for( String line : new FileLineReader( is ) ) {
				try {
					ranking.add( Long.parseLong( line ) );
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
			this.ranking = ranking;
//			aiRanking.put( aiParticipants.get( 0 ), ranking );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	private <T> String getOption( String key, String def ) {
		String val = options.get( key );
		if( val == null ) {
			return def;
		}
		return val;
	}

	@Override
	public List<Long> getAIParticipants() {
		return this.aiParticipants;
	}

	@Override
	public Map<Long, List<Long>> getAIRanking( Long userId ) {
		Map<Long, List<Long>> aiRanking = new HashMap<>();
		GAGameDetails details = db.getGameInfo( this.gameId );
		for( Long cid : details.getCriteriaWeights().keySet() ) {
			aiRanking.put( cid, this.ranking );
		}
		return aiRanking;
	}

	@Override
	public double getWeight( Long userId ) {
		if( this.aiParticipants.contains( userId ) ) {
			String val = getOption( "weight", "1" );
			try {
				return Double.parseDouble( val );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
		return -1;
	}

}
