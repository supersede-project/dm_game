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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="criterias_matrices_data")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CriteriasMatrixData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long criteriasMatrixDataId;

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="game_id", nullable = false)
	private Game game;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="criteria_row_id", nullable = false)
	private ValutationCriteria rowCriteria;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="criteria_column_id", nullable = false)
	private ValutationCriteria columnCriteria;

	@Column(nullable = false)
	private Long value;
	 
	public CriteriasMatrixData() {    	
	}
	
    public Long getCriteriasMatrixDataId() {
        return criteriasMatrixDataId;
    }
 
    public void setCriteriasMatrixDataId(Long criteriasMatrixDataId) {
        this.criteriasMatrixDataId = criteriasMatrixDataId;
    }
    
    public Game getGame() {
        return game;
    }
 
    public void setGame(Game game) {
        this.game = game;
    }
    
    public ValutationCriteria getRowCriteria() {
        return rowCriteria;
    }
 
    public void setRowCriteria(ValutationCriteria rowCriteria) {
        this.rowCriteria = rowCriteria;
    }
    
    public ValutationCriteria getColumnCriteria() {
        return columnCriteria;
    }
 
    public void setColumnCriteria(ValutationCriteria columnCriteria) {
        this.columnCriteria = columnCriteria;
    }
    
    public Long getValue() {
        return value;
    }
 
    public void setValue(Long value) {
        this.value = value;
    }
}
