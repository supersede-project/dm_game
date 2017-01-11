package eu.supersede.dm.ga.data;

public class GAGameSummary
{
    private Long id;
    private Long		owner;
    private String		date	= "";
    private String		status	= "open";

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getOwner()
    {
        return this.owner;
    }

    public void setOwner(Long owner)
    {
        this.owner = owner;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}