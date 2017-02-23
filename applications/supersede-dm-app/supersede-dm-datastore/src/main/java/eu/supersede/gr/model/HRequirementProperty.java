package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_requirement_properties")
public class HRequirementProperty
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long reqId;
    private String name;
    private String value;
    // private Double priority;

    public Long getGameId()
    {
        return id;
    }

    public void setGameId(Long id)
    {
        this.id = id;
    }

    public Long getReqId()
    {
        return reqId;
    }

    public void setReqId(Long reqId)
    {
        this.reqId = reqId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}