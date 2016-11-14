/**
 * 
 */
package eu.supersede.dm.iga.problem;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;

/**
 * @author fitsum
 *
 */
public class ReadAHPTest {

	@Test
	public void test() {
		String ahpVotesFile = "resources/input/VOTES/System.csv";
		String dependenciesFile = "resources/input/dependencies";
		ObjectiveFunction of = ObjectiveFunction.CRITERIA;
		GAVariant gaVariant = GAVariant.SO;
		SingleObjectivePrioritizationProblem problem = 
				new SingleObjectivePrioritizationProblem(ahpVotesFile, dependenciesFile, of, gaVariant);
	}

}
