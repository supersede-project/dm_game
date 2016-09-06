package eu.supersede.dm.algorithms;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.*;


public class Ahp extends Algorithm{

	//attributes
	//DMA: These numbers we need to generalize, maybe also char it in the constructor
	final int[] numbersScale = new int[] { 9, 7, 5, 3, 1, 3, 5, 7, 9 };
	AHPStructure _ahpInput;
	
	//constructors
	public Ahp() {}
	
	public Ahp(AHPStructure input){
		_ahpInput = input;
	} 
	
	//methods 
	private double[][] fillComputeMatrix( Matrix<Integer> _matrixValue ) {
		
		double[][] _table = new double[_matrixValue.getRowHeaders().size()][_matrixValue.getRowHeaders().size()];

		//Initialize to 1
		for (int i=0; i< _matrixValue.getRowHeaders().size(); i++){
			_table[i][i] = 1.0;
		}
		//Give values in pairs
		int val1, val2;
        for(int c=1; c < _matrixValue.getRowHeaders().size(); c++ ){
			for( int r=0; r < c; r++ ) {
				val1 = _matrixValue.getValue( r, c, 4 );
				val2 = _matrixValue.getValue( c, r, 4 );
				if(val1>val2){
					_table[r][c] = (double)numbersScale[val1];
					_table[c][r] = 1/(double)numbersScale[val1];
				}else{
					_table[c][r] = (double)numbersScale[val2];
					_table[r][c] = 1/(double)numbersScale[val2];
				}
			}
		}
		
		return _table;
	}

	private void sumVectors(double[] v1, double[] v2){
		if(v1.length != v2.length){ 
			throw new RuntimeException( "Sum of vectors: vectors are not the same size" );
		}
		for(int i =0; i< v2.length; i++){
			v1[i] = v1[i] + v2[i];
		}
	}
	
	private void productVectorWeight(double[] v1, double w){
		for(int i=0; i < v1.length; i++ ){
			v1[i] = v1[i] * w;	
		}
	}
	
	/*
	private void printVector(String nVector, double[] v1){
		System.out.println("\n Start:" + nVector);
		for(int i=0; i < v1.length; i++ ){
			System.out.print(v1[i] + "  ");
		}
		System.out.println("\n Fin:" + nVector);
	}
	
	private void printArray(String nArray, double[][] a1){
		for(int i=0; i < a1.length; i++ ){
			printVector(nArray, a1[i]);
		}
	}
	*/
	
	private double[] ahpEigen(RealMatrix rMatrix){
		
		EigenDecomposition objEig = new EigenDecomposition(rMatrix);
		
		double[] eigenvalues = objEig.getRealEigenvalues();
		//printVector("Eigenvalues", eigenvalues);
		
		//Obtain the index of max value
		int indexMaxEigval = 0;
		double maxEigval = 0.0;
		for(int i=0; i < eigenvalues.length; i++){
			if(maxEigval < Math.abs(eigenvalues[i])){
				maxEigval = Math.abs(eigenvalues[i]);
				indexMaxEigval = i;
			}
		}
		//System.out.println("Index: " + indexMaxEigval);
		
		RealVector rank = objEig.getEigenvector(indexMaxEigval);
		//printVector("Eigenvector", rank.toArray());
		
		//double norm = rank.getL1Norm(); //calculate the norm
		double norm=0;
		double rankarray[]=rank.toArray();
		for (int i=0;i<rank.getDimension();i++){
			norm+=rankarray[i];
		}
		//System.out.println("Norm: " + norm);
		
		//Define the normalized rank 
		double[] nrank = new double[rMatrix.getRowDimension()];
		for(int i=0; i< rMatrix.getRowDimension(); i++){
			nrank[i] = (double)rank.getEntry(i)/(double)norm;
		}
		return nrank;
	}
	
	@Override
	public Map<String, Double> execute() {
		//System.out.println( "Running AHP" );

		double[] weights, optRankCriteria, sumrank; 

		//Table with sum of weighted ranks for the options/alternatives
		sumrank = new double[_ahpInput.getOptionsCount()];
		
		//Calculate the weights of criteria
		double[][] m1 = fillComputeMatrix(_ahpInput.getCriteriaMatrix());
		//printArray("Array Criteria", m1);
		
		Array2DRowRealMatrix arrayCr = new Array2DRowRealMatrix(m1);
		weights = ahpEigen(arrayCr.copy());
		//printVector("Criteria", weights);
		
		//Calculate the ranks of options for each criteria
		Array2DRowRealMatrix arrayOpt;
		
		double[][] m2;
		int i = 0;
		Map<String, Double> result = new HashMap<String, Double>();
		
		for(String criteria: _ahpInput.getCriteria()){
			m2 = fillComputeMatrix(_ahpInput.getOptionsMatrix().get(criteria));
			//printArray("Array Option", m2);
			
			arrayOpt = new Array2DRowRealMatrix(m2);
			optRankCriteria = ahpEigen(arrayOpt.copy());
			//printVector("Option by Criteria" + criteria, optRankCriteria);
			
			//Multiply vector and weight
			productVectorWeight(optRankCriteria, weights[i]);
			
			//Sum of vector in sumrank variable
			sumVectors(sumrank, optRankCriteria);
			i++;
		}
		//printVector("Result Rank", sumrank);
		
		//build the result Map
		int j=0;
		for(String option: _ahpInput.getOptions()){
			result.put(option, sumrank[j]);
			j++;
		}		
		return result;
	}
}