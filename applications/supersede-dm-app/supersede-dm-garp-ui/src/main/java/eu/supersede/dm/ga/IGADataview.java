package eu.supersede.dm.ga;

import java.util.List;
import java.util.Map;

import eu.supersede.gr.data.GAGameDetails;
import eu.supersede.gr.data.GAGameSummary;

public interface IGADataview {

	GAGameDetails		create(GAGameSummary game);

	void			create(GAGameSummary game, List<String> criteria, List<Long> requirements, List<Long> participants);

	List<GAGameSummary>	getOwnedGames(Long owner);

	List<GAGameSummary>	getActiveGames(Long userId);

//	GAGameSummary			getActiveGame(Long userId);

	void			setRanking(Long gameId, Long userId, Map<String, List<Long>> reqs);

	List<Long>		getRankingsCriterion(Long gameId, Long userId, String criterion);

	Map<String, List<Long>> getRanking(Long gameId, Long userId);

	GAGameDetails		getGameInfo(Long gameId);

	List<Long>		getParticipants(GAGameSummary game);

	List<Long>		getParticipants(Long gameId);

	List<String>	getCriteria(GAGameSummary game);

	List<String>	getCriteria(long gameId);

	List<Long>		getRequirements(Long gameId);

	Map<String, List<Long>> getRequirements(Long gameId, Long userId);

}