package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HRequirementDependency;

public interface RequirementsDependenciesJpa extends JpaRepository<HRequirementDependency, Long> {

    @Query("SELECT d FROM HRequirementDependency d WHERE dependerId = ?1")
    List<HRequirementDependency> findDependenciesByDependerId( Long reqId );
    
    @Query("SELECT d FROM HRequirementDependency d WHERE dependeeId = ?1")
    List<HRequirementDependency> findDependenciesByDependeeId( Long reqId );
    
}
