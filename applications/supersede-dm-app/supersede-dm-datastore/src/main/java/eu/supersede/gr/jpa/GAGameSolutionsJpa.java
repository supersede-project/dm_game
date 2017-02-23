package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HGASolution;

public interface GAGameSolutionsJpa extends JpaRepository<HGASolution, Long>
{
    HGASolution findByGameId(Long gameId);
}