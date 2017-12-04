package eu.supersede.dm;

import java.time.Duration;

import eu.supersede.dm.OrchestratorUtil.ValueDMFitnessCalculator;
import eu.supersede.dm.methods.AHPRequirementsPrioritizationMethod;

public class RulesetGenerator {
	
	public DMRuleset generateTestRuleset( Integer accuracy )
	{
		DMRuleset ruleset = new DMRuleset();

		ruleset.addRule( "Prefer GA over AHP when the number of requirements increases", new ValueDMFitnessCalculator<Integer>( accuracy )
		{
			
			@Override
			public DMFitness evaluate(DMActivityConfiguration act, DMProblem problem)
			{
//				System.out.println( act );
				switch( value ) {
				case 0: // No precision; maximum speed
				{
					if (act.getMethodName().equals(AHPRequirementsPrioritizationMethod.NAME)) {
						return new DMFitness(Integer.MAX_VALUE);
					}
					return new DMFitness(0);
				}
				case 1: // Prefer time over precision
				{
					if (act.getMethodName().equals(AHPRequirementsPrioritizationMethod.NAME)) {
						return new DMFitness(problem.getRequirements().size()*2);
					}
					return new DMFitness(5);
				}
				case 3: // Prefer precision over time
				{
					if (act.getMethodName().equals(AHPRequirementsPrioritizationMethod.NAME)) {
						return new DMFitness((problem.getRequirements().size() /2));
					}
					return new DMFitness(10);
				}
				case 4: // No hurry; maximum precision
				{
					if (act.getMethodName().equals(AHPRequirementsPrioritizationMethod.NAME)) {
						return new DMFitness(0);
					}
					return new DMFitness(Integer.MAX_VALUE);
				}
				case 2: default: // Balance precision and time
				{
					if (act.getMethodName().equals(AHPRequirementsPrioritizationMethod.NAME)) {
						return new DMFitness(problem.getRequirements().size());
					}
					return new DMFitness(5);
				}
				}
			}
		});

		ruleset.addRule( "Prefer gamification if there is a close deadline", new IDMFitnessCalculator()
		{
			@Override
			public DMFitness evaluate(DMActivityConfiguration cfg, DMProblem problem)
			{
//				System.out.println( cfg );
				Duration d = problem.getDeadline();
				if (d != null)
				{
					if (d.toHours() < 24)
					{
						if ("on".equals(cfg.getOption("gamification")))
						{
							return new DMFitness(0);
						}
						else
						{
							return new DMFitness(1);
						}
					}
				}
				return new DMFitness();
			}
		});

		ruleset.addRule( "Only select methods that macth the desired objective", new IDMFitnessCalculator()
		{
			@Override
			public DMFitness evaluate(DMActivityConfiguration cfg, DMProblem problem)
			{
//				System.out.println( cfg );
				for (DMMethod m : problem.getAvailableMethods())
				{
					if (cfg.getMethodName().equals(m.getName()))
					{
						if (m.getObjective() == problem.getObjective())
						{
							return new DMFitness(0);
						}
					}
				}
				return new DMFitness(Integer.MAX_VALUE);
			}
		});

		return ruleset;
	}
	
}
