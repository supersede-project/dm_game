package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HGAGameSummary;

public interface GAGameSummaryJpa extends JpaRepository<HGAGameSummary, Long>
{

    // @Query("SELECT game FROM ga_games WHERE owner = ?1")
    // List<GAGameSummary> findByOwner( Long playerId );

}
