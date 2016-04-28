package demo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="games_players_points")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GamePlayerPoint{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gamePlayerPointId;	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;
	
    private Long points;
     
    public Long getGamePlayerPointId(){
    	return gamePlayerPointId;
    }
    
    public void setGamePlayerPointId(Long gamePlayerPointId){
    	this.gamePlayerPointId = gamePlayerPointId;
    }
    
    public GamePlayerPoint(){	
    }
    
    public Long getPoints(){
    	return points;
    }
    
    public void setPoints(Long points){
    	this.points = points;
    }
    
    @JsonIgnore
    public User getUser(){
    	return user;
    }
    
    public void setUser(User user){
    	this.user = user;
    }
    
    public Game getGame(){
    	return game;
    }
    
    public void setGame(Game game){
    	this.game = game;
    }
}