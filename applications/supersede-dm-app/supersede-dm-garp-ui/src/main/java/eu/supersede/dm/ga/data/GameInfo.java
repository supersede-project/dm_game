package eu.supersede.dm.ga.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInfo
{
    private GAGame game;
    private List<String> criteria;
    private List<Long> requirements;
    private List<Long> participants;
    private Map<Long, Map<String, List<Long>>> rankings;

    public GameInfo()
    {
        criteria = new ArrayList<>();
        requirements = new ArrayList<>();
        participants = new ArrayList<>();
        rankings = new HashMap<>();
    }

    public GAGame getGame()
    {
        return game;
    }

    public void setGame(GAGame game)
    {
        this.game = game;
    }

    public List<String> getCriteria()
    {
        return criteria;
    }

    public void setCriteria(List<String> criteria)
    {
        this.criteria = criteria;
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