package eu.supersede.dm.ga.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.dm.ga.db.HAttribute;

public interface AttributesJpa extends JpaRepository<HAttribute, Long> {}
