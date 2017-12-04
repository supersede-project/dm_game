package eu.supersede.dm.ga;

import java.util.List;
import java.util.Map;

public interface RankingAssistant {

	public List<Long> getAIParticipants();

	public Map<Long, List<Long>> getAIRanking(Long userId);

	public double getWeight( Long userId );

}
