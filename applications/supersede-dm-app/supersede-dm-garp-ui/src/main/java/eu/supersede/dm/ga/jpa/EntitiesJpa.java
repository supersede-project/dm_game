package eu.supersede.dm.ga.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.dm.ga.db.HEntity;

public interface EntitiesJpa extends JpaRepository<HEntity, Long> {}
