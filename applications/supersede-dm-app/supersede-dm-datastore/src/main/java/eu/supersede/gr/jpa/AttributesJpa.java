package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HAttribute;

public interface AttributesJpa extends JpaRepository<HAttribute, Long> {}
