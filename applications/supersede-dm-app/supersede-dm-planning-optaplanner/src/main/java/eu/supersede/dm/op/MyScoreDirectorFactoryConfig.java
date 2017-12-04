package eu.supersede.dm.op;

import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;

import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;

public class MyScoreDirectorFactoryConfig extends ScoreDirectorFactoryConfig {
	
	private DMRuleset ruleset;
	private DMProblem problem;
	
	public MyScoreDirectorFactoryConfig( DMProblem problem, DMRuleset ruleset ) {
		this.problem = problem;
		this.ruleset = ruleset;
	}
	
	protected <Solution_> AbstractScoreDirectorFactory<Solution_> buildEasyScoreDirectorFactory() {
        if (easyScoreCalculatorClass != null) {
            @SuppressWarnings("unchecked")
			EasyScoreCalculator<Solution_> easyScoreCalculator = 
				(EasyScoreCalculator<Solution_>)new ProcessPlanEasyScoreCalculator( problem, ruleset );
            return new EasyScoreDirectorFactory<>(easyScoreCalculator);
        } else {
            return null;
        }
    }
	
}
