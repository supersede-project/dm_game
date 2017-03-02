package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import eu.supersede.gr.model.id.ProcessMemberId;

@Entity
@IdClass(ProcessMemberId.class)
@Table(name = "h_process_members")
public class HProcessMember
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Id
    private Long userId;

    @Id
    private Long processId;

    private String role;

    public HProcessMember()
    {

    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }
}