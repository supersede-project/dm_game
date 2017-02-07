package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HActivity;

public interface JpaActivities extends JpaRepository<HActivity, Long> {

}
