package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HPropertyBag;

public interface PropertyBagsJpa extends JpaRepository<HPropertyBag, Long>
{
	
}