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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for ValutationCriteria.
*/

@Entity
@Table(name = "valutation_criterias")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ValutationCriteria
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long criteriaId;
    private String name;
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valutationCriteria")
    private List<UserCriteriaPoint> userCriteriaPoints;

    public ValutationCriteria()
    {
    }

    public ValutationCriteria(String name)
    {
        this.name = name;
    }

    public Long getCriteriaId()
    {
        return criteriaId;
    }

    public void setCriteriaId(Long criteriaId)
    {
        this.criteriaId = criteriaId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @JsonIgnore
    public List<UserCriteriaPoint> getUserCriteriaPoints()
    {
        return userCriteriaPoints;
    }

    public void setUserCriteriaPoints(List<UserCriteriaPoint> userCriteriaPoints)
    {
        this.userCriteriaPoints = userCriteriaPoints;
    }
}