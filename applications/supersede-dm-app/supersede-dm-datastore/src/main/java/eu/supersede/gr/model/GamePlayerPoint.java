///*
//   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//*/
//
//package eu.supersede.gr.model;
//
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//@Entity
//@Table(name = "games_players_points")
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
//public class GamePlayerPoint
//{
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long gamePlayerPointId;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "game_id", nullable = false)
//    private HAHPGame game;
//
//    private Long points;
//    private Long agreementIndex;
//    private Long positionInVoting;
//    private Long virtualPosition;
//
//    public Long getGamePlayerPointId()
//    {
//        return gamePlayerPointId;
//    }
//
//    public void setGamePlayerPointId(Long gamePlayerPointId)
//    {
//        this.gamePlayerPointId = gamePlayerPointId;
//    }
//
//    public GamePlayerPoint()
//    {
//    }
//
//    public Long getPoints()
//    {
//        return points;
//    }
//
//    public void setPoints(Long points)
//    {
//        this.points = points;
//    }
//
//    @JsonIgnore
//    public User getUser()
//    {
//        return user;
//    }
//
//    public void setUser(User user)
//    {
//        this.user = user;
//    }
//
//    public HAHPGame getGame()
//    {
//        return game;
//    }
//
//    public void setGame(HAHPGame game)
//    {
//        this.game = game;
//    }
//
//    public Long getAgreementIndex()
//    {
//        return agreementIndex;
//    }
//
//    public void setAgreementIndex(Long agreementIndex)
//    {
//        this.agreementIndex = agreementIndex;
//    }
//
//    public Long getPositionInVoting()
//    {
//        return positionInVoting;
//    }
//
//    public void setPositionInVoting(Long positionInVoting)
//    {
//        this.positionInVoting = positionInVoting;
//    }
//
//    public Long getVirtualPosition()
//    {
//        return virtualPosition;
//    }
//
//    public void setVirtualPosition(Long virtualPosition)
//    {
//        this.virtualPosition = virtualPosition;
//    }
//}