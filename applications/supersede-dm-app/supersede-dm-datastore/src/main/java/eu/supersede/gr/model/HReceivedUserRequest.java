package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_received_userrequests")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HReceivedUserRequest
{
    @Id
    private String id;
    private String alertId;
    private String classification;
    private double accuracy;
    private String description;
    private int positiveSentiment;
    private int negativeSentiment;
    private int overallSentiment;
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

    public int getPositiveSentiment()
    {
        return positiveSentiment;
    }

    public void setPositiveSentiment(int positiveSentiment)
    {
        this.positiveSentiment = positiveSentiment;
    }

    public int getNegativeSentiment()
    {
        return negativeSentiment;
    }

    public void setNegativeSentiment(int negativeSentiment)
    {
        this.negativeSentiment = negativeSentiment;
    }

    public int getOverallSentiment()
    {
        return overallSentiment;
    }

    public void setOverallSentiment(int overallSentiment)
    {
        this.overallSentiment = overallSentiment;
    }
}