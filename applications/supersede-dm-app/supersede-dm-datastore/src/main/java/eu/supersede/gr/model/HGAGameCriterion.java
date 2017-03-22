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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "h_ga_game_criteria")
public class HGAGameCriterion
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long gameId;
    private Long criterionId;
    private Double criterionWeight;
    private Boolean inverse = false;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;

    HGAGameCriterion()
    {
    }

    public HGAGameCriterion(Long gameid, Long criterionid, Double criterionWeight)
    {
        this.gameId = gameid;
        this.criterionId = criterionid;
        this.criterionWeight = criterionWeight;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public Long getCriterionId()
    {
        return criterionId;
    }

    public void setCriterionId(Long criterionId)
    {
        this.criterionId = criterionId;
    }

    public Double getCriterionWeight()
    {
        return criterionWeight;
    }

    public void setCriterionWeight(Double criterionWeight)
    {
        this.criterionWeight = criterionWeight;
    }

    public Boolean isInverse()
    {
        return inverse;
    }

    public void setInverse(Boolean inverse)
    {
        this.inverse = inverse;
    }

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}