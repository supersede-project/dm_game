package jmt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.supersede.dm.DMFitness;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMProcessConfig;
import eu.supersede.dm.DMRequirement;
import eu.supersede.dm.DMRole;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.DMSkill;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.DMTopic;
import eu.supersede.dm.DMUser;
import eu.supersede.dm.ExecutionMode;
import eu.supersede.dm.IDMFitnessCalculator;
import eu.supersede.dm.IDMPlanner;
import eu.supersede.dm.InvocationMode;
import eu.supersede.dm.PrioritizationApproach;

public class DMJMetalTest {
	
	public static void main(String[] args) {
		
		DMRuleset ruleset = new DMRuleset();
		
		ruleset.addRule( new IDMFitnessCalculator() {
			@Override
			public DMFitness evaluate( DMProcessConfig cfg, DMProblem problem) {
				Duration d = problem.getDeadline();
				if( d != null ) {
					if( d.toHours() < 24 ) {
						if( "on".equals( cfg.getOption( "gamification" ) ) ) {
							return new DMFitness( 1 );
						}
					}
				}
				return new DMFitness();
			}
		});
		
		DMProblem.Config c = new DMProblem.Config();
		
		DMTopic userImpact = new DMTopic( "User Impact" );
		DMTopic developmentEffort = new DMTopic( "Development Effort" );
		
		{
			DMTopic[] topics = new DMTopic[] { userImpact, developmentEffort };
			Random random = new Random( System.currentTimeMillis() );
			for( int i = 0; i < 20; i++ ) {
				c.add( new DMRequirement( "Requirement " + random.nextInt(), topics[random.nextInt( 2 )] ) );
			}
		}
		
		c.addUser( new DMUser( "John", new DMSkill[] {
				new DMSkill( userImpact, 0.8 ),
				new DMSkill( developmentEffort, 0.2 )
		} ) );
		
		c.addUser( new DMUser( "Jim", new DMSkill[] {
				new DMSkill( userImpact, 0.6 ),
				new DMSkill( developmentEffort, 0.4 )
		} ) );
		
		c.addUser( new DMUser( "Jack", new DMSkill[] {
				new DMSkill( userImpact, 0.5 ),
				new DMSkill( developmentEffort, 0.5 )
		} ) );
		
		c.add( DMMethod.create(
				"AHP Session",
				PrioritizationApproach.Pairwise, 
				ExecutionMode.BackgroundSession, 
				InvocationMode.OneShot,
				new DMRoleSpec[] { 
						new DMRoleSpec( new DMRole( "Opinion Provider" ), 1, -1 ), 
								new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) }  ) );
//		c.add( DMMethod.create(
//				"IAHP Session",
//				PrioritizationApproach.Pairwise, 
//				ExecutionMode.BackgroundSession, 
//				InvocationMode.OneShot,
//				new DMRoleSpec[] { 
//						new DMRoleSpec( new DMRole( "Opinion Provider" ), 1, -1 ) } ) );
		
		c.setDeadline( Duration.ofHours( 8 ) );
		
		c.addOption( new DMOption( "gamification", new String[] { "on", "off" } ) );
		c.addOption( new DMOption( "negotiator", new String[] { "active", "not active" } ) );
		
		c.setConstraint( "requirements", "" + c.getRequirements().size() );
		
//		{
//			List<String> list = new ArrayList<>();
//			for( DMMethod t : problem.methods() ) {
//				list.add( t.getName() );
//			}
//			table.setValues( "algorithm", list );
//		}
		
		DMProblem problem = new DMProblem( c );
		
		IDMPlanner planner = new DMJMetal( ruleset );
		
		DMSolution solution = planner.solve( problem );
		
		if( solution != null ) {
			System.out.println( solution );
		}
		
//		SolverFactory<DMSolution> solverFactory = SolverFactory.createFromXmlResource( SOLVER_CONFIG_XML, DMApp.class.getClassLoader() );
//		Solver<DMSolution> solver = solverFactory.buildSolver();
//		
//		DMSolution unsolvedSolution = new DMSolution();
//		
//		// Solve the problem
//		solver.solve( unsolvedSolution );
//		
//		DMSolution solution = solver.getBestSolution();
//		
//		// Display the result
//		System.out.println( solution );
	}
	
}
