package demo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Requirement;
import demo.model.RequirementsMatrixData;
import demo.model.ValutationCriteria;

public interface RequirementsMatricesDataJpa extends JpaRepository<RequirementsMatrixData, Long> {

	List<RequirementsMatrixData> findByCriteria(ValutationCriteria criteria);

	List<RequirementsMatrixData> findByRowRequirement(Requirement requirement);

	List<RequirementsMatrixData> findByColumnRequirement(Requirement requirement);

}
