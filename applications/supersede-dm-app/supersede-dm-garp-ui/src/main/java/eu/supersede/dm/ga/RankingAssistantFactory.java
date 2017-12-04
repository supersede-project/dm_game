package eu.supersede.dm.ga;

public class RankingAssistantFactory {
	
	private static RankingAssistantFactory instance = new RankingAssistantFactory();
	
	public static RankingAssistantFactory get() {
		return instance;
	}
	
	public RankingAssistant createRankingAssistant( GAPersistentDB db, Long gameId ) {
		return new FilesystemRankingAssistant( db, gameId );
	}
	
}
