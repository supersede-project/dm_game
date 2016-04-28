package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.ValutationCriteria;

public interface ValutationCriteriaJpa extends JpaRepository<ValutationCriteria, Long> {

}
