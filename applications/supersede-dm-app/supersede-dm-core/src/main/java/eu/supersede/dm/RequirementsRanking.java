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

package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.HRequirementScore;

public class RequirementsRanking
{
    private Long id;
    private Long processId;
    private String name;
    private Boolean selected;
    private List<HRequirementScore> scores;

    public RequirementsRanking()
    {
        scores = new ArrayList<>();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public List<HRequirementScore> getScores()
    {
        return scores;
    }

    public void setScores(List<HRequirementScore> scores)
    {
        this.scores = scores;
    }
}