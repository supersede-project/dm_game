package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.RequirementChoice;

public interface RequirementsChoicesJpa extends JpaRepository<RequirementChoice, Long> {

}
