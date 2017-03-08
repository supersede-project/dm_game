package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_alerts")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HAlert
{
    @Id
    private String id;
    private String applicationId;
    private Long processId;
    private Long timestamp;

    public HAlert()
    {
    }

    public HAlert(String id, String applicationId, long timestamp)
    {
        this.id = id;
        this.applicationId = applicationId;
        this.timestamp = timestamp;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }
}