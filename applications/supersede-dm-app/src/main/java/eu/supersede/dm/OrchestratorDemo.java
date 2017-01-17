package eu.supersede.dm;

import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.supersede.dm.jmetal.DMJMetalMultiMethodPlanner;
import eu.supersede.dm.methods.AHPRequirementsPrioritizationMethod;
import eu.supersede.dm.methods.DMLibrary;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@Service
public class OrchestratorDemo
{
	@Autowired
	private RequirementsJpa requirements;

	@Autowired
	private ValutationCriteriaJpa criteria;

	@Autowired
	private UsersJpa users;

	public OrchestratorDemo()
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
		DMRuleset ruleset = new DMRuleset();

		ruleset.addRule( "Prefer GA over AHP when the number of requirements increases", new ValueDMFitnessCalculator<Integer>( accuracy )
		{
			
			@Override
			public DMFitness evaluate(DMActivity act, DMProblem problem)
			{
				System.out.println( act );
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
			public DMFitness evaluate(DMActivity cfg, DMProblem problem)
			{
				System.out.println( cfg );
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
			public DMFitness evaluate(DMActivity cfg, DMProblem problem)
			{
				System.out.println( cfg );
				for (DMMethod m : problem.getAvailableMethods())
				{
					if (cfg.methodName.equals(m.getName()))
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

	public DMSolution plan( Integer accuracy )
	{
		return planWithRealData( accuracy );
	}

	public DMSolution planWithRealData( Integer accuracy )
	{

		DMProblem.Config cfg = new DMProblem.Config();

		for (User user : users.findAll())
		{
			cfg.addUser(new DMUser(user.getName(), new DMSkill[] {}));
		}

		List<DMTopic> topics = new ArrayList<>();

		{
			for (ValutationCriteria criterion : criteria.findAll())
			{
				topics.add(new DMTopic(criterion.getName()));
			}
		}

		{
			for (Requirement r : requirements.findAll())
			{
				cfg.add(new DMRequirement("" + r.getRequirementId(), r.getName()));
			}
		}

		for (DMMethod method : DMLibrary.get().methods())
		{
			cfg.add(method);
		}

		cfg.setDeadline(Duration.ofHours(8));

		cfg.addOption(new DMOption("gamification", new String[] { "on", "off" }));
		cfg.addOption(new DMOption("negotiator", new String[] { "active", "not active" }));

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
				new DMJMetalMultiMethodPlanner(ruleset);

		DMSolution solution = planner.solve(problem);

		print(solution, System.out);

		return solution;

	}

	public DMSolution planWithRandomData()
	{

		PrintStream out = System.out;

		Random random = new Random(System.currentTimeMillis());

		// Should be loaded by reflection
		// DMLibrary.get().addMethod( new GAMethod() );

		// Define global rules
		DMRuleset ruleset =  this.createTestRuleset( 2 );

		// Setup problem configuration
		DMProblem.Config c = new DMProblem.Config();

		// Criteria
		DMTopic userImpact = new DMTopic("User Impact");
		DMTopic developmentEffort = new DMTopic("Development Effort");

		// Some requirements with random topics
		{
			DMTopic[] topics = new DMTopic[] { userImpact, developmentEffort };
			for (int i = 0; i < 15; i++)
			{
				c.add(new DMRequirement("R" + i, "Requirement " + random.nextInt(10000), topics[random.nextInt(2)]));
			}
		}

		// Users
		c.addUser(new DMUser("John",
				new DMSkill[] { new DMSkill(userImpact, 0.8), new DMSkill(developmentEffort, 0.2) }));

		c.addUser(
				new DMUser("Jim", new DMSkill[] { new DMSkill(userImpact, 0.6), new DMSkill(developmentEffort, 0.4) }));

		c.addUser(new DMUser("Jack",
				new DMSkill[] { new DMSkill(userImpact, 0.5), new DMSkill(developmentEffort, 0.5) }));

		// Available methods
		for (DMMethod method : DMLibrary.get().methods())
		{
			c.add(method);
		}

		c.setDeadline(Duration.ofHours(8));

		c.addOption(new DMOption("gamification", new String[] { "on", "off" }));
		c.addOption(new DMOption("negotiator", new String[] { "active", "not active" }));

		c.setConstraint("requirements", "" + c.getRequirements().size());

		DMProblem problem = new DMProblem(DMObjective.PrioritizeRequirements, c);

		out.println("Requirements:");
		for (DMRequirement r : problem.getRequirements())
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

		IDMPlanner planner =
				// new DMRandomPlanner();
				// new DMJMetalSingleMethodPlanner( ruleset );
				new DMJMetalMultiMethodPlanner(ruleset);

		DMSolution solution = planner.solve(problem);

		return solution;

	}

	public void print(DMProblem problem, PrintStream out)
	{

		out.println("Requirements:");
		for (DMRequirement r : problem.getRequirements())
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

	public void run(PrintStream out)
	{

		Random random = new Random(System.currentTimeMillis());

		// Should be loaded by reflection
		// DMLibrary.get().addMethod( new GAMethod() );

		// Define global rules
		DMRuleset ruleset = this.createTestRuleset( 2 );

		// Setup problem configuration
		DMProblem.Config c = new DMProblem.Config();

		// Criteria
		DMTopic userImpact = new DMTopic("User Impact");
		DMTopic developmentEffort = new DMTopic("Development Effort");

		// Some requirements with random topics
		{
			DMTopic[] topics = new DMTopic[] { userImpact, developmentEffort };
			for (int i = 0; i < 15; i++)
			{
				c.add(new DMRequirement("R" + i, "Requirement " + random.nextInt(10000), topics[random.nextInt(2)]));
			}
		}

		// Users
		c.addUser(new DMUser("John",
				new DMSkill[] { new DMSkill(userImpact, 0.8), new DMSkill(developmentEffort, 0.2) }));

		c.addUser(
				new DMUser("Jim", new DMSkill[] { new DMSkill(userImpact, 0.6), new DMSkill(developmentEffort, 0.4) }));

		c.addUser(new DMUser("Jack",
				new DMSkill[] { new DMSkill(userImpact, 0.5), new DMSkill(developmentEffort, 0.5) }));

		// Available methods
		for (DMMethod method : DMLibrary.get().methods())
		{
			c.add(method);
		}

		c.setDeadline(Duration.ofHours(8));

		c.addOption(new DMOption("gamification", new String[] { "on", "off" }));
		c.addOption(new DMOption("negotiator", new String[] { "active", "not active" }));

		c.setConstraint("requirements", "" + c.getRequirements().size());

		DMProblem problem = new DMProblem(DMObjective.PrioritizeRequirements, c);

		out.println("Requirements:");
		for (DMRequirement r : problem.getRequirements())
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

		IDMPlanner planner =
				// new DMRandomPlanner();
				// new DMJMetalSingleMethodPlanner( ruleset );
				new DMJMetalMultiMethodPlanner(ruleset);

		DMSolution solution = planner.solve(problem);

		if (solution != null)
		{
			out.println("Selected solution: " + solution);
		}

		// Execute process
		for (DMStep step : solution.steps)
		{

			for (DMActivity activity : step.activities)
			{

				DMMethod method = DMLibrary.get().getMethod(activity.methodName);

				if (method == null)
				{
					continue;
				}

				DMStatus status = new DMStatus();

				for (DMRequirement r : problem.getRequirements())
				{
					status.addRequirement(r);
				}

				ProcessClient client = clients.get(activity.methodName);

				if (client != null)
				{
					client.manage(status, method);
				}

			}

		}

	}

	Map<String, ProcessClient> clients = new HashMap<>();

	public interface ProcessClient
	{
		public void manage(DMStatus status, DMMethod method);
	}

	// public static class AHPRPProcessClient implements ProcessClient {
	//
	// Map<String,Class<?>> taskClients = new HashMap<>();
	//
	// public AHPRPProcessClient() {
	// taskClients.put( "gameSetup", TSupervisorCreateGame.class );
	// taskClients.put( "vote", TOpinionProviderVote.class );
	// taskClients.put( "negotiate", TNegotiatorResolve.class );
	// taskClients.put( "gameFinalization", TSupervisorTerminateGame.class );
	// }
	//
	// TaskClient createTaskClient( DMTask task, AHPRequirementsPrioritizationMethod method, DMStatus status ) {
	// Class<?> cls = taskClients.get( task.getId() );
	// TaskClient client = null;
	// try {
	// client = (TaskClient)cls.newInstance();
	// client.setMethod( method );
	// client.setTask( task );
	// client.setStatus( status );
	// } catch (InstantiationException | IllegalAccessException e) {
	// e.printStackTrace();
	// }
	// return client;
	// }
	//
	// @Override
	// public void manage( DMStatus status, DMMethod generic_method ) {
	//
	// AHPRequirementsPrioritizationMethod method = (AHPRequirementsPrioritizationMethod) generic_method;
	//
	// method.init( status );
	//
	// Set<String> done = new HashSet<>();
	//
	// while( !method.isComplete( status ) ) {
	//
	// List<DMTask> tasks = method.getActiveTasks( status );
	//
	//// out.println( "Found " + tasks.size() + " active tasks" );
	//
	// while( tasks.size() > 0 ) {
	//
	// DMTask task = tasks.get( 0 );
	//
	// tasks.remove( 0 );
	//
	// if( done.contains( task.getId() ) ) {
	// continue;
	// }
	//
	// done.add( task.getId() );
	//
	// TaskClient taskClient = createTaskClient( task, method, status );
	//
	//// taskClient.run();
	//
	// Thread thread = new Thread( taskClient );
	// thread.setDaemon( true );
	// thread.start();
	//
	// }
	//
	// }
	//
	// out.println( "done" );
	//
	// }
	//
	// }

	public static abstract class TaskClient implements Runnable
	{

		AHPRequirementsPrioritizationMethod method;
		DMTask task;
		DMStatus status;

		public void setMethod(AHPRequirementsPrioritizationMethod method)
		{
			this.method = method;
		}

		public void setStatus(DMStatus status)
		{
			this.status = status;
		}

		public void setTask(DMTask task)
		{
			this.task = task;
		}

		public DMTask getTask()
		{
			return this.task;
		}

	}

	// public static class TSupervisorCreateGame extends TaskClient {
	//
	// AHPRequirementsPrioritizationMethod getMethod() {
	// return (AHPRequirementsPrioritizationMethod)method;
	// }
	//
	// @Override
	// public void run() {
	// out.println( getTask().getId() + ": Game master opens the game" );
	// getMethod().createGame( status );
	// getMethod().completeTask( status, getTask() );
	// }
	//
	// }
	//
	// public static class TSupervisorTerminateGame extends TaskClient {
	//
	// AHPRequirementsPrioritizationMethod getMethod() {
	// return (AHPRequirementsPrioritizationMethod)method;
	// }
	//
	// @Override
	// public void run() {
	// try {
	// Thread.sleep( 1000 );
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// out.println( getTask().getId() + ": Game master calculates the final rank" );
	// getMethod().callAHP( status );
	// out.println( getTask().getId() + ": Game master closes the game" );
	// getMethod().completeTask( status, getTask() );
	// }
	//
	// }

	// public static class TOpinionProviderVote extends TaskClient {
	//
	// AHPRequirementsPrioritizationMethod getMethod() {
	// return (AHPRequirementsPrioritizationMethod)method;
	// }
	//
	// @Override
	// public void run() {
	// out.println( getTask().getId() + ": Users voting" );
	// try {
	// Thread.sleep( 1000 );
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// getMethod().completeTask( status, getTask() );
	// }
	//
	// }
	//
	// public static class TNegotiatorResolve extends TaskClient {
	//
	// AHPRequirementsPrioritizationMethod getMethod() {
	// return (AHPRequirementsPrioritizationMethod)method;
	// }
	//
	// @Override
	// public void run() {
	// out.println( getTask().getId() + ": Negotiator resolves conflicts" );
	// try {
	// Thread.sleep( 1000 );
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// getMethod().completeTask( status, getTask() );
	// }
	//
	// }

}
