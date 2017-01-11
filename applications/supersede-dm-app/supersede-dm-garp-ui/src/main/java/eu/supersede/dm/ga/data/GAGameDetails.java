package eu.supersede.dm.ga.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ga_game_details")
public class GAGameDetails
{
	GAGameSummary	game;
    List<String>	criteria = new ArrayList<>();
    List<Long>		requirements = new ArrayList<>();
    List<Long>		participants = new ArrayList<>();
    Map<Long, Map<String,List<Long>>> rankings = new HashMap<>();
    
    public GAGameSummary getGame() {
		return game;
	}
	public void setGame(GAGameSummary game) {
		this.game = game;
	}
	public List<String> getCriteria() {
		return criteria;
	}
	public void setCriteria(List<String> criteria) {
		this.criteria = criteria;
	}
	public List<Long> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<Long> requirements) {
		this.requirements = requirements;
	}
	public List<Long> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Long> participants) {
		this.participants = participants;
	}
	public Map<Long, Map<String, List<Long>>> getRankings() {
		return rankings;
	}
	public void setRankings(Map<Long, Map<String, List<Long>>> rankings) {
		this.rankings = rankings;
	}
}