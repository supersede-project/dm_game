package eu.supersede.dm;

import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import eu.supersede.dm.op.DMOptaPlanner;
import eu.supersede.gr.model.HTopic;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Service
public class OrchestratorUtil
{

	public OrchestratorUtil()
	{
		// clients.put( AHPRequirementsPrioritizationMethod.NAME, new AHPRPProcessClient() );
	}

	public static abstract class ValueDMFitnessCalculator<T> implements IDMFitnessCalculator {
		T value;
		public ValueDMFitnessCalculator( T value ) {
			this.value  = value;
		}
	}

	public DMRuleset createTestRuleset( Integer accuracy )
	{
		return new RulesetGenerator().generateTestRuleset( accuracy );
	}

	public DMSolution plan( Integer accuracy )
	{
		return planWithRealData( accuracy );
	}

	public DMSolution planWithRealData( Integer accuracy )
	{

		DMProblem.Config cfg = new DMProblem.Config();

		for (User user : DMGame.get().getJpa().users.findAll())
		{
			cfg.addUser(new DMUser( user.getName() ));
		}
		
		List<HTopic> topics = new ArrayList<>();

		{
			for (ValutationCriteria criterion : DMGame.get().getJpa().criteria.findAll())
			{
				topics.add(new HTopic(criterion.getName()));
			}
		}

		{
			for (Requirement r : DMGame.get().getJpa().requirements.findAll())
			{
				cfg.add( r );
//				cfg.add(new DMRequirement("" + r.getRequirementId(), r.getName()));
			}
		}

		for (DMMethod method : DMLibrary.get().methods())
		{
			cfg.add(method);
		}

		cfg.setDeadline(Duration.ofHours(8));

//		cfg.addOption(new DMOption("gamification", new String[] { "on", "off" }));
//		cfg.addOption(new DMOption("negotiator", new String[] { "active", "not active" }));

		cfg.setConstraint("requirements", "" + cfg.getRequirements().size());

		return planWithData(cfg, createTestRuleset( accuracy ), DMObjective.PrioritizeRequirements);
	}

	public DMSolution planWithData(DMProblem.Config cfg, DMRuleset ruleset, DMObjective obj)
	{

		DMProblem problem = new DMProblem(obj, cfg);

		print(problem, System.out);

		IDMPlanner planner =
				// new DMRandomPlanner();
				// new DMJMetalSingleMethodPlanner( ruleset );
//				new DMJMetalMultiMethodPlanner(ruleset);
		new DMOptaPlanner( ruleset );

		DMSolution solution = planner.solve(problem);

		print(solution, System.out);

		return solution;

	}

	public void print(DMProblem problem, PrintStream out)
	{

		out.println("Requirements:");
		for (Requirement r : problem.getRequirements())
		{
			// out.println( r.getId() + ": " + r.getText() );
			out.println(r);
		}

		out.println("Candidate users:");
		for (DMUser u : problem.users())
		{
			// out.println( u.getName() );
			out.println(u);
		}

		out.println("Candidate methods:");
		for (DMMethod method : problem.methods())
		{
			out.println(method.getName());
		}

	}

	public void print(DMSolution solution, PrintStream out)
	{
		if (solution != null)
		{
			out.println("Selected solution: " + solution);
		}
	}
	
	// Unused
//	public void run(PrintStream out)
//	{
//
//		Random random = new Random(System.currentTimeMillis());
//
//		// Should be loaded by reflection
//		// DMLibrary.get().addMethod( new GAMethod() );
//
//		// Define global rules
//		DMRuleset ruleset = this.createTestRuleset( 2 );
//
//		// Setup problem configuration
//		DMProblem.Config c = new DMProblem.Config();
//
//		// Criteria
//		HTopic userImpact = new HTopic("User Impact");
//		HTopic developmentEffort = new HTopic("Development Effort");
//
//		// Some requirements with random topics
//		{
////			HTopic[] topics = new HTopic[] { userImpact, developmentEffort };
//			for (int i = 0; i < 15; i++)
//			{
//				Requirement r = new Requirement();
//				r.setRequirementId( (long)i );
//				r.setName( "Requirement " + random.nextInt(10000) );
//				c.add( r );
////				c.add( new DMRequirement(
////						"R" + i, 
////						"Requirement " + random.nextInt(10000), 
////						topics[random.nextInt(2)]));
//			}
//		}
//
//		// Users
//		c.addUser(new DMUser("John",
//				new HUserSkill[] { new HUserSkill(userImpact, 0.8), new HUserSkill(developmentEffort, 0.2) }));
//
//		c.addUser(
//				new DMUser("Jim", new HUserSkill[] { new HUserSkill(userImpact, 0.6), new HUserSkill(developmentEffort, 0.4) }));
//
//		c.addUser(new DMUser("Jack",
//				new HUserSkill[] { new HUserSkill(userImpact, 0.5), new HUserSkill(developmentEffort, 0.5) }));
//
//		// Available methods
//		for (DMMethod method : DMLibrary.get().methods())
//		{
//			c.add(method);
//		}
//
//		c.setDeadline(Duration.ofHours(8));
//
//		c.addOption(new DMOption("gamification", new String[] { "on", "off" }));
//		c.addOption(new DMOption("negotiator", new String[] { "active", "not active" }));
//
//		c.setConstraint("requirements", "" + c.getRequirements().size());
//
//		DMProblem problem = new DMProblem(DMObjective.PrioritizeRequirements, c);
//
//		out.println("Requirements:");
//		for (Requirement r : problem.getRequirements())
//		{
//			// out.println( r.getId() + ": " + r.getText() );
//			out.println(r);
//		}
//
//		out.println("Candidate users:");
//		for (DMUser u : problem.users())
//		{
//			// out.println( u.getName() );
//			out.println(u);
//		}
//
//		out.println("Candidate methods:");
//		for (DMMethod method : problem.methods())
//		{
//			out.println(method.getName());
//		}
//
//		IDMPlanner planner =
//				// new DMRandomPlanner();
//				// new DMJMetalSingleMethodPlanner( ruleset );
//				new DMJMetalMultiMethodPlanner(ruleset);
//
//		DMSolution solution = planner.solve(problem);
//
//		if (solution != null)
//		{
//			out.println("Selected solution: " + solution);
//		}
//
//	}

}
