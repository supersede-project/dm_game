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

package eu.supersede.dm.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.gr.model.HGAGameSummary;

public class GAGameDetails
{
    private HGAGameSummary game;
    private List<Long> requirements;
    private Map<Long, Double> criteriaWeights;
    private List<Long> opinionProviders;
    private List<Long> negotiators;
    private Map<Long, Map<Long, Double>> playerWeights;
    private Map<Long, Map<Long, List<Long>>> rankings;

    public GAGameDetails()
    {
        requirements = new ArrayList<>();
        criteriaWeights = new HashMap<>();
        opinionProviders = new ArrayList<>();
        negotiators = new ArrayList<>();
        playerWeights = new HashMap<>();
        rankings = new HashMap<>();
    }

    public HGAGameSummary getGame()
    {
        return game;
    }

    public void setGame(HGAGameSummary game)
    {
        this.game = game;
    }

    public List<Long> getRequirements()
    {
        return requirements;
    }

    public void setRequirements(List<Long> requirements)
    {
        this.requirements = requirements;
    }

    public Map<Long, Double> getCriteriaWeights()
    {
        return criteriaWeights;
    }

    public void setCriteriaWeights(Map<Long, Double> criteriaWeights)
    {
        this.criteriaWeights = criteriaWeights;
    }

    public List<Long> getNegotiators()
    {
        return negotiators;
    }

    public void setNegotiators(List<Long> negotiators)
    {
        this.negotiators = negotiators;
    }

    public List<Long> getOpinionProviders()
    {
        return opinionProviders;
    }

    public void setOpinionProviders(List<Long> opinionProviders)
    {
        this.opinionProviders = opinionProviders;
    }

    public Map<Long, Map<Long, Double>> getPlayerWeights()
    {
        return playerWeights;
    }

    public void setPlayerWeights(Map<Long, Map<Long, Double>> playerWeights)
    {
        this.playerWeights = playerWeights;
    }

    public Map<Long, Map<Long, List<Long>>> getRankings()
    {
        return rankings;
    }

    public void setRankings(Map<Long, Map<Long, List<Long>>> rankings)
    {
        this.rankings = rankings;
    }
}