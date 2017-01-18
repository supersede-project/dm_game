package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HEntity;

public interface EntitiesJpa extends JpaRepository<HEntity, Long> {}
