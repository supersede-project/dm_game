package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HRequirementDependency;

public interface RequirementsDependenciesJpa extends JpaRepository<HRequirementDependency, Long>
{
    @Query("SELECT d FROM HRequirementDependency d WHERE requirementId = ?1")
    List<HRequirementDependency> findByRequirementId(Long requirementId);
}