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

/**
* @author Andrea Sosi
**/

package eu.supersede.gr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for JudgeAct.
*/

@Entity
@Table(name = "judge_acts")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class JudgeAct
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long judgeActId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requirements_matrix_data_id", nullable = false)
    private RequirementsMatrixData requirementsMatrixData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "judge_id")
    private User judge;

    private Boolean voted;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date votedTime;

    public JudgeAct()
    {

    }

    public Long getJudgeActId()
    {
        return judgeActId;
    }

    public void setJudgeActId(Long judgeActId)
    {
        this.judgeActId = judgeActId;
    }

    public RequirementsMatrixData getRequirementsMatrixData()
    {
        return requirementsMatrixData;
    }

    public void setRequirementsMatrixData(RequirementsMatrixData requirementsMatrixData)
    {
        this.requirementsMatrixData = requirementsMatrixData;
    }

    public User getJudge()
    {
        return judge;
    }

    public void setJudge(User judge)
    {
        this.judge = judge;
    }

    public Boolean getVoted()
    {
        return voted;
    }

    public void setVoted(Boolean voted)
    {
        this.voted = voted;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public Date getVotedTime()
    {
        return votedTime;
    }

    public void setVotedTime(Date votedTime)
    {
        this.votedTime = votedTime;
    }
}