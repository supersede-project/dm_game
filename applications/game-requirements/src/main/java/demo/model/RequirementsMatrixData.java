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

package demo.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
	Model class for RequirementsMatrixData.
*/

@Entity
@Table(name="requirements_matrices_data")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementsMatrixData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long requirementsMatrixDataId;

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="game_id", nullable = false)
	private Game game;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="criteria_id", nullable = false)
	private ValutationCriteria criteria;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="requirement_row_id", nullable = false)
	private Requirement rowRequirement;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="requirement_column_id", nullable = false)
	private Requirement columnRequirement;
	
	@Column(nullable = false)
	private Long value;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "requirementsMatrixData")
	private List<PlayerMove> playerMoves;
	
	public RequirementsMatrixData() {    	
	}
	
    public Long getRequirementsMatrixDataId() {
        return requirementsMatrixDataId;
    }
 
    public void setRequirementsMatrixDataId(Long requirementsMatrixDataId) {
        this.requirementsMatrixDataId = requirementsMatrixDataId;
    }

    public Game getGame() {
        return game;
    }
 
    public void setGame(Game game) {
        this.game = game;
    }
    
    public ValutationCriteria getCriteria() {
        return criteria;
    }
 
    public void setCriteria(ValutationCriteria criteria) {
        this.criteria = criteria;
    }
    
    public Requirement getRowRequirement() {
        return rowRequirement;
    }
 
    public void setRowRequirement(Requirement rowRequirement) {
        this.rowRequirement = rowRequirement;
    }
    
    public Requirement getColumnRequirement() {
        return columnRequirement;
    }
 
    public void setColumnRequirement(Requirement columnRequirement) {
        this.columnRequirement = columnRequirement;
    }
    
    public Long getValue() {
        return value;
    }
 
    public void setValue(Long value) {
        this.value = value;
    }

    @JsonIgnore
	public List<PlayerMove> getPlayerMoves() {
		return playerMoves;
	}

    @JsonIgnore
	public void setPlayerMoves(List<PlayerMove> playerMoves) {
		this.playerMoves = playerMoves;
	}
}
