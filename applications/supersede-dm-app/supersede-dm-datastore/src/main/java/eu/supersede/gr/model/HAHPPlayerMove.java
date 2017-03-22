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

@Entity
@Table(name = "player_moves")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HAHPPlayerMove
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playerMoveId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requirements_matrix_data_id", nullable = false)
    private HAHPRequirementsMatrixData requirementsMatrixData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    private Long value;
    private Boolean played;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date playedTime;

    public HAHPPlayerMove()
    {

    }

    public Long getPlayerMoveId()
    {
        return playerMoveId;
    }

    public void setPlayerMoveId(Long playerMoveId)
    {
        this.playerMoveId = playerMoveId;
    }

    public HAHPRequirementsMatrixData getRequirementsMatrixData()
    {
        return requirementsMatrixData;
    }

    public void setRequirementsMatrixData(HAHPRequirementsMatrixData requirementsMatrixData)
    {
        this.requirementsMatrixData = requirementsMatrixData;
    }

    public User getPlayer()
    {
        return player;
    }

    public void setPlayer(User player)
    {
        this.player = player;
    }

    public Long getValue()
    {
        return value;
    }

    public void setValue(Long value)
    {
        this.value = value;
    }

    public Boolean getPlayed()
    {
        return played;
    }

    public void setPlayed(Boolean played)
    {
        this.played = played;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public Date getPlayedTime()
    {
        return playedTime;
    }

    public void setPlayedTime(Date playedTime)
    {
        this.playedTime = playedTime;
    }
}