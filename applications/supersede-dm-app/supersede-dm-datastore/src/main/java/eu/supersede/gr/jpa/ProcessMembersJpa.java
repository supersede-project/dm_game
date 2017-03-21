package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import eu.supersede.gr.model.HProcessMember;

public interface ProcessMembersJpa extends JpaRepository<HProcessMember, Long>
{
    @Query("SELECT m FROM HProcessMember m WHERE processId = ?1")
    List<HProcessMember> findProcessMembers(Long processId);

    @Modifying
    @Transactional
    @Query("DELETE FROM HProcessMember m where id = ?1 AND userId = ?2 AND processId = ?3")
    void deleteById(Long id, Long userId, Long processId);
}