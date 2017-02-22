package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HRequirementProperty;

public interface RequirementsPropertiesJpa extends JpaRepository<HRequirementProperty, Long> {
	
    @Query("SELECT p FROM HRequirementProperty p WHERE reqId = ?1")
    List<HRequirementProperty> findPropertiesByRequirementId( Long reqId );
    
}
