/*
(C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_received_user_requests")
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