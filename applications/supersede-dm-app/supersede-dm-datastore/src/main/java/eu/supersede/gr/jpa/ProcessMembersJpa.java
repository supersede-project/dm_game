package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HProcessMember;

public interface ProcessMembersJpa extends JpaRepository<HProcessMember, Long> {

    @Query("SELECT m FROM HProcessMember m WHERE processId = ?1")
    List<HProcessMember> findProcessMembers( Long processId );
    
}
