/**
 * 
 */
package eu.supersede.dm.iga.encoding;

import java.util.ArrayList;
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
public class RequirementsPrioritizationSolution extends
		AbstractGenericSolution<String, PermutationProblem<PermutationSolution<?>>> implements
		PermutationSolution<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8291497080139627973L;
	
	
	/** Constructor */
	public RequirementsPrioritizationSolution(PermutationProblem<PermutationSolution<?>> problem) {
		super(problem);

		overallConstraintViolationDegree = 0.0;
		numberOfViolatedConstraints = 0;

		List<String> randomSequence = new ArrayList<String>(problem.getPermutationLength());

		for (int j = 0; j < problem.getPermutationLength(); j++) {
			randomSequence.add("R"+j);
		}

		java.util.Collections.shuffle(randomSequence);

		for (int i = 0; i < getNumberOfVariables(); i++) {
			setVariableValue(i, randomSequence.get(i));
		}
	}

	

	/** Copy Constructor */
	public RequirementsPrioritizationSolution(RequirementsPrioritizationSolution solution) {
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
		return getVariableValue(index).toString(); //already a String
	}

//	@Override
	public RequirementsPrioritizationSolution copy() {
		return new RequirementsPrioritizationSolution(this);
	}
	
	
	
	public String[] getVariablesArray(){
		return solutionToArray(this);
	}
	
	private String[] solutionToArray(RequirementsPrioritizationSolution sol){
		String[] arr = new String[sol.getNumberOfVariables()];
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
		List<String> labels = ((AbstractPrioritizationProblem)problem).getOptions();
		
		String string;

		string = "";
		for (int i = 0; i < getNumberOfVariables(); i++)
			string += labels.get(i) + "; ";

		return string;
	} // toString
	
	/**
	 * Returns a string representing the object
	 * 
	 * @return The string
	 */
	public String toNamedStringWithObjectives() {
		List<String> labels = ((AbstractPrioritizationProblem)problem).getOptions();
		
		String string;

		string = "";
		for (int i = 0; i < getNumberOfObjectives(); i++){
			string += getObjective(i) + ",";
		}
		for (int i = 0; i < getNumberOfVariables(); i++)
			string += labels.get(i) + "; ";

		return string;
	} // toString
	
}
