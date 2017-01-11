package eu.supersede.dm.ga.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.dm.ga.data.GAGameSummary;
import eu.supersede.dm.ga.db.HGAGameSummary;

public interface GAGameSummaryJpa extends JpaRepository<HGAGameSummary, Long> {
	
//	@Query("SELECT game FROM ga_games WHERE owner = ?1")
//	List<GAGameSummary> findByOwner( Long playerId );
	
}
