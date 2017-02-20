package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HProcessCriterion;

public interface ProcessCriteriaJpa extends JpaRepository<HProcessCriterion, Long> {

    @Query("SELECT c FROM HProcessCriterion c WHERE processId = ?1")
    List<HProcessCriterion> findProcessCriteria( Long processId );
    
}
