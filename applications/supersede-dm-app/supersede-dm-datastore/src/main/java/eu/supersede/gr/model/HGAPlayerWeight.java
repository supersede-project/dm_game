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
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import eu.supersede.gr.model.id.PlayerWeightId;

@Entity
@IdClass(PlayerWeightId.class)
@Table(name = "h_ga_players_weights")
public class HGAPlayerWeight
{
    @Id
    private Long gameId;

    @Id
    private Long criterionId;

    @Id
    private Long userId;

    private Double weight;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;

    public HGAPlayerWeight()
    {

    }

    public HGAPlayerWeight(Long gameId, Long criterionId, Long userId, Double weight)
    {
        this.gameId = gameId;
        this.criterionId = criterionId;
        this.userId = userId;
        this.weight = weight;
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

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setWeight(Double weight)
    {
        this.weight = weight;
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