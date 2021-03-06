package eu.supersede.dm.ahp.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AHPStructure {
	
	//DMA: Add this global variable, maybe we can use it as property that is charged in the constructor
	final int scale = 8;
	
	List<String>				criteria = new ArrayList<>();
	List<String>				options = new ArrayList<>();
	Matrix<Integer>				criteriaMatrix = new Matrix<Integer>();
	Map<String,Matrix<Integer>>	optionsMatrices = new HashMap<>();
	
	public AHPStructure() {}
	
	public int getCriteriaCount() {
		return this.criteriaMatrix.getColumnHeaders().size();
	}
	
	public int getOptionsCount() {
		return this.options.size();
	}
	
	
	public List<String> getOptions() {
		return this.options;
	}
	
	public List<String> getCriteria() {
		return this.criteria;
	}

	//DMA: Added method
	public Matrix<Integer> getCriteriaMatrix(){
		return this.criteriaMatrix;
	}
	
	//DMA: Added method
	public Map<String,Matrix<Integer>> getOptionsMatrix(){
		return this.optionsMatrices;
	}
	
	public void setCriteria( List<String> values ) {
		
		criteriaMatrix.clear();
		optionsMatrices.clear();
		for( String val : values ) {
			criteriaMatrix.addRow( val );
			criteriaMatrix.addColumn( val );
		}
		this.criteria = values;
	}
	
	public void setCriteria( String ... values ) {
		
		setCriteria( Arrays.asList( values ) );
		
	}
	
	public void setPreference( String crit1, String crit2, int value ) {
		this.criteriaMatrix.setValue( crit1, crit2, scale - value );
		this.criteriaMatrix.setValue( crit2, crit1, value );
	}
	
	public void setOptions( List<String> values ) {
		
		this.optionsMatrices.clear();
		
		for( int i = 0; i < this.getCriteriaCount(); i++ ) {
			Matrix<Integer> m = new Matrix<Integer>();
			for( String val : values ) {
				m.addColumn( val );
				m.addRow( val );
			}
			this.optionsMatrices.put( this.criteriaMatrix.getColumnHeaders().get( i ), m );
		}
		
		this.options = values;
	}
	
	public void setOptions( String ... values ) {
		
		setOptions( Arrays.asList( values ) );
		
	}
	
	public void setOptionPreference( String opt1, String opt2, String criterion, int value ) {
		
		Matrix<Integer> m = this.optionsMatrices.get( criterion );
		if( m == null ) {
			throw new RuntimeException( "Attempting to set a non-existent criterion: " + criterion );
		}
		m.setValue( opt1, opt2, scale - value );
		m.setValue( opt2, opt1, value );
	}
	
}
