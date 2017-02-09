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

package eu.supersede.gr.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GAGameDetails
{
    private GAGameSummary game;
    private HashMap<Long, Double> criteriaWeights = new HashMap<>();
    private List<Long> requirements = new ArrayList<>();
    private List<Long> participants = new ArrayList<>();
    private Map<Long, Map<String, List<Long>>> rankings = new HashMap<>();

    public GAGameSummary getGame()
    {
        return game;
    }

    public void setGame(GAGameSummary game)
    {
        this.game = game;
    }

    public HashMap<Long, Double> getCriteriaWeights()
    {
        return criteriaWeights;
    }

    public void setCriteriaWeights(HashMap<Long, Double> criteriaWeights)
    {
        this.criteriaWeights = criteriaWeights;
    }

    public List<Long> getRequirements()
    {
        return requirements;
    }

    public void setRequirements(List<Long> requirements)
    {
        this.requirements = requirements;
    }

    public List<Long> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<Long> participants)
    {
        this.participants = participants;
    }

    public Map<Long, Map<String, List<Long>>> getRankings()
    {
        return rankings;
    }

    public void setRankings(Map<Long, Map<String, List<Long>>> rankings)
    {
        this.rankings = rankings;
    }
}