package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_received_user_requests")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HReceivedUserRequest
{
    @Id
    private String id;
    private String alertId;
    private String classification;
    private Double accuracy;
    private String description;
    private Integer positiveSentiment;
    private Integer negativeSentiment;
    private Integer overallSentiment;
    // private String[] feedbackIDs;
    // private String[] features;

    public HReceivedUserRequest()
    {
    }

    public HReceivedUserRequest(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAlertId()
    {
        return alertId;
    }

    public void setAlertId(String alertId)
    {
        this.alertId = alertId;
    }

    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    public double getAccuracy()
    {
        return accuracy;
    }

    public void setAccuracy(double accuracy)
    {
        this.accuracy = accuracy;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getPositiveSentiment()
    {
        return positiveSentiment;
    }

    public void setPositiveSentiment(Integer positiveSentiment)
    {
        this.positiveSentiment = positiveSentiment;
    }

    public Integer getNegativeSentiment()
    {
        return negativeSentiment;
    }

    public void setNegativeSentiment(Integer negativeSentiment)
    {
        this.negativeSentiment = negativeSentiment;
    }

    public Integer getOverallSentiment()
    {
        return overallSentiment;
    }

    public void setOverallSentiment(Integer overallSentiment)
    {
        this.overallSentiment = overallSentiment;
    }
}