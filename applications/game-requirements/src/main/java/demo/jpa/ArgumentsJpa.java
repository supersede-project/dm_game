package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Argument;

public interface ArgumentsJpa extends JpaRepository<Argument, Long> {

}
