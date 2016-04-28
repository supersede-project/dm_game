package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Requirement;

public interface RequirementsJpa extends JpaRepository<Requirement, Long> {

}
