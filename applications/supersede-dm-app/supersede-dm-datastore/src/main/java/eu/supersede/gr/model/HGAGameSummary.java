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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import eu.supersede.gr.data.GAGameSummary;

@Entity
@Table(name = "h_ga_games")
public class HGAGameSummary
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Long activityId;
    
    String name = "";
    
    @Column(name = "owner")
    private Long owner;

    @Column(name = "date")
    private String date = "";

    @Column(name = "status")
    private String status = "open";

    public HGAGameSummary() {

    }

    public HGAGameSummary(Long activityId, GAGameSummary game) {
    	this.activityId = activityId;
        this.id = game.getId();
        this.owner = game.getOwner();
        this.date = game.getDate();
        this.status = game.getStatus();
        this.name = game.getName();
    }
    
    public Long getActivityId() {
    	return this.activityId;
    }
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwner() {
        return this.owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getName() {
    	return this.name;
    }
}