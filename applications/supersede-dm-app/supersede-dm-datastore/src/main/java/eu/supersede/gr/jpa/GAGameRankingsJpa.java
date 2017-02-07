package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HGARankingInfo;

public interface GAGameRankingsJpa extends JpaRepository<HGARankingInfo,Long> {
	
	@Query("SELECT jsonizedRanking FROM HGARankingInfo rankings WHERE gameId = ?1 AND userId = ?2")
	public String findRankingByGameAndUser( Long gameId, Long userId );
	
}
