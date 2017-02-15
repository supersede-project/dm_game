package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.HProcessMember;

public interface ProcessMembersJpa extends JpaRepository<HProcessMember, Long> {

}
