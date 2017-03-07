package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import eu.supersede.gr.model.HProcessCriterion;

public interface ProcessCriteriaJpa extends JpaRepository<HProcessCriterion, Long>
{
    @Query("SELECT c FROM HProcessCriterion c WHERE processId = ?1")
    List<HProcessCriterion> findByProcessId(Long processId);

    @Modifying
    @Transactional
    @Query("DELETE FROM HProcessCriterion c WHERE criterionId = ?1 AND sourceId = ?2 AND processId = ?3")
    void deleteById(Long criterionId, Long sourceId, Long processId);
}