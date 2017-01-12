package eu.supersede.gr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eu.supersede.gr.data.GAGameSummary;

@Entity
@Table(name = "ga_games")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HGAGameSummary
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "owner")
    private Long owner;

    @Column(name = "date")
    private String date = "";
    // @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    // @Temporal(TemporalType.TIMESTAMP)
    // private Date startTime;

    @Column(name = "status")
    private String status = "open";

    public HGAGameSummary()
    {

    }

    public HGAGameSummary(GAGameSummary game)
    {
        this.id = game.getId();
        this.owner = game.getOwner();
        this.date = game.getDate();
        this.status = game.getStatus();
    }

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