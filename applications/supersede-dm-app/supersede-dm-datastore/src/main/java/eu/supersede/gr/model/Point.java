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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for Point.
*/

@Entity
@Table(name="points")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Point {

	@Id
    private Long pointId;	
	private String description;
	private Long globalPoints;
	private Long criteriaPoints;
	
	public Point(){
	}
	
	public Long getPointId() {
		return pointId;
	}

	public void setPointId(Long pointId) {
		this.pointId = pointId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getGlobalPoints() {
		return globalPoints;
	}

	public void setGlobalPoints(Long globalPoints) {
		this.globalPoints = globalPoints;
	}

	public Long getCriteriaPoints() {
		return criteriaPoints;
	}

	public void setCriteriaPoints(Long criteriaPoints) {
		this.criteriaPoints = criteriaPoints;
	}	
}
