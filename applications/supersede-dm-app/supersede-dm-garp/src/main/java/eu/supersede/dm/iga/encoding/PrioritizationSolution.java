/**
 * 
 */
package eu.supersede.dm.iga.encoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;

/**
 * @author fitsum
 *
 */
public class PrioritizationSolution extends
		AbstractGenericSolution<Integer, PermutationProblem<?>> implements
		PermutationSolution<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8291497080139627973L;

	private static String[] criteriaNames;
	private static int index = 0;
	
	/** Constructor */
	public PrioritizationSolution(PermutationProblem<?> problem) {
		super(problem);

		overallConstraintViolationDegree = 0.0;
		numberOfViolatedConstraints = 0;

		List<Integer> randomSequence = new ArrayList<Integer>(
				problem.getPermutationLength());

		for (int j = 0; j < problem.getPermutationLength(); j++) {
			randomSequence.add(j);
		}

		java.util.Collections.shuffle(randomSequence);

		for (int i = 0; i < getNumberOfVariables(); i++) {
			setVariableValue(i, randomSequence.get(i));
		}
		
		criteriaNames = new String[getNumberOfObjectives()];
		index = 0;
	}

	public void addCriterionName (String name, int idx){
		criteriaNames[idx] = name;
	}
	public void addCriterionName (String name){
		criteriaNames[index++] = name;
	}

	public String getCriterionName (int index){
		return criteriaNames[index];
	}
	
	/** Copy Constructor */
	public PrioritizationSolution(PrioritizationSolution solution) {
		super(solution.problem);
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i));
		}

		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			setVariableValue(i, solution.getVariableValue(i));
		}

		overallConstraintViolationDegree = solution.overallConstraintViolationDegree;
		numberOfViolatedConstraints = solution.numberOfViolatedConstraints;

		attributes = new HashMap<Object, Object>(solution.attributes);
	}

//	@Override
	public String getVariableValueString(int index) {
		return AbstractPrioritizationProblem.REQUIREMENT_IDS.get(getVariableValue(index));
	}

//	@Override
	public PrioritizationSolution copy() {
		return new PrioritizationSolution(this);
	}
	
	
	
	public int[] getVariablesArray(){
		return solutionToArray(this);
	}
	
	public String[] getVariablesStringArray(){
		String[] stringArray = new String[getNumberOfVariables()];
		for (int i = 0;i < getNumberOfVariables(); i++){
			stringArray[i] = getVariableValueString(i);
		}
		return stringArray;
	}
	
	private int[] solutionToArray(PrioritizationSolution sol){
		int[] arr = new int[sol.getNumberOfVariables()];
		for (int i = 0;i < sol.getNumberOfVariables(); i++){
			arr[i] = sol.getVariableValue(i);
		}
		return arr;
	}
	
	/**
	 * Returns a string representing the object
	 * 
	 * @return The string
	 */
	public String toNamedString() {
//		List<String> labels = ((AbstractPrioritizationProblem)problem).getOptions();
		
		String string;

		string = "";
		for (int i = 0; i < getNumberOfVariables(); i++)
			string += getVariableValueString(i) + "; ";

		return string;
	} // toString
	
	/**
	 * Returns a string representing the object
	 * 
	 * @return The string
	 */
	public String toNamedStringWithObjectives() {
		
		String string;

		string = "";
		for (int i = 0; i < getNumberOfObjectives(); i++){
			string += getObjective(i) + ",";
		}
		string += toNamedString();

		return string;
	} // toString
	
	public int hashCode (){
		return toNamedString().hashCode();
	}
	
	public Map<String, Double> toRanks (){
		Map<String, Double> ranks = new HashMap<String, Double> ();
		int numVars = getNumberOfVariables();
		for (int i = 0; i < numVars; i++){
			ranks.put(getVariableValueString(i), new Double(numVars - i)); //numVars - i ==> weight/score of the requirement: i.e., the higher the value the higher the rank
		}
		return ranks;
	}
	
	/* (non-Javadoc)
	 * @see org.uma.jmetal.solution.impl.AbstractGenericSolution#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o){
			return true;
		}
		if (o == null || !(o instanceof PrioritizationSolution)){
			return false;
		}
		PrioritizationSolution other = ((PrioritizationSolution)o);
		if (Arrays.equals(getVariablesArray(), other.getVariablesArray())){
			return true;
		}else{
			return false;
		}
		
	}
}
