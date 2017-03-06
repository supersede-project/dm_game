package eu.supersede.dm.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Alert
{
    private String id;
    private String applicationId;
    private Long timestamp;
    private String tenant;
    private List<Condition> conditions;
    private List<UserRequest> requests;

    public Alert()
    {
        conditions = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public Alert(String id, String applicationId, long timestamp, String tenant, List<Condition> conditions,
            List<UserRequest> requests)
    {
        super();
        this.id = id;
        this.applicationId = applicationId;
        this.timestamp = timestamp;
        this.tenant = tenant;
        this.conditions = conditions;
        this.requests = requests;
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

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public List<Condition> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<Condition> conditions)
    {
        this.conditions = conditions;
    }

    public List<UserRequest> getRequests()
    {
        return requests;
    }

    public void setRequests(List<UserRequest> requests)
    {
        this.requests = requests;
    }
}