package demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="player_moves")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlayerMove {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long playerMoveId;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="requirements_matrix_data_id", nullable = false)
	private RequirementsMatrixData requirementsMatrixData;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="player_id", nullable = false)
	private User player;
	
	private Long value;
	private Boolean played;
	
	@Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date playedTime;
	 
	public PlayerMove() {    	
	}
	
    public Long getPlayerMoveId() {
        return playerMoveId;
    }
 
    public void setPlayerMoveId(Long playerMoveId) {
        this.playerMoveId = playerMoveId;
    }
    
    public RequirementsMatrixData getRequirementsMatrixData() {
        return requirementsMatrixData;
    }
 
    public void setRequirementsMatrixData(RequirementsMatrixData requirementsMatrixData) {
        this.requirementsMatrixData = requirementsMatrixData;
    }
    
    public User getPlayer() {
        return player;
    }
 
    public void setPlayer(User player) {
        this.player = player;
    }

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Boolean getPlayed() {
		return played;
	}

	public void setPlayed(Boolean played) {
		this.played = played;
	}
	
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    public Date getPlayedTime() {
        return playedTime;
    }
 
    public void setPlayedTime(Date playedTime) {
        this.playedTime = playedTime;
    }	
}
