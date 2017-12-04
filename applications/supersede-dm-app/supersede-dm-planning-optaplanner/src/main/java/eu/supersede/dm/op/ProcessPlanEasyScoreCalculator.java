package eu.supersede.dm.op;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMFitness;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.IDMFitnessCalculator;

public class ProcessPlanEasyScoreCalculator implements EasyScoreCalculator<ProcessChain> {
	
	
	interface ActivityScoreCalculator {
		public HardSoftScore calculateScore( ActivityExecution ae, ProcessChain processPlan );
	}
	
	boolean log = false;
	
	Map<String,ActivityScoreCalculator> scoreCalculators = new HashMap<>();
	
	DMRuleset ruleset;
	DMProblem problem;
	
	
	public ProcessPlanEasyScoreCalculator( DMProblem problem, DMRuleset ruleset ) {
		this();
		this.problem = problem;
		this.ruleset = ruleset;
	}
	
	public ProcessPlanEasyScoreCalculator() {
//		scoreCalculators.put( "enact", new ActivityScoreCalculator() {
//			@Override
//			public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
//				if( ae.getNext() != null ) {
//					return HardSoftScore.valueOf( -Integer.MAX_VALUE, 0 );
//				}
//				return null; //HardSoftScore.valueOf( 0, 0 );
//			}} );
//		scoreCalculators.put( "", new ActivityScoreCalculator() {
//			@Override
//			public HardSoftScore calculateScore( ActivityExecution ae, ProcessChain processPlan) {
//				int softScore = 0;
//				return null;
//			}} );
		scoreCalculators.put( "ahp-based prioritization", new ActivityScoreCalculator() {
		@Override
		public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
			return HardSoftScore.valueOf( 0, -1 );
		}} );
		scoreCalculators.put( "g.a.-based prioritization", new ActivityScoreCalculator() {
		@Override
		public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
			return HardSoftScore.valueOf( 0, -1 );
		}} );
	}
	
	DMFitness calculateFitness( ProcessChain process ) {
		DMFitness fitness = new DMFitness();
		return fitness;
	}
	
	@Override
	public HardSoftScore calculateScore( ProcessChain processPlan ) {
		int hardScore = 0;
		int softScore = 0;

		if( log ) {
			System.out.print( ">>> " + processPlan.getProcessPath() );
			System.out.println();
		}
		
		DMFitness fitness = new DMFitness();
		
		
		int size = 0;
		
		for( ProcessActivity a = processPlan.getProcessPath(); a != null; a = a.getNext() ) {
			
			size++;
			
			if( a instanceof ActivityExecution ) {
				ActivityExecution ae = (ActivityExecution)a;
				softScore++;
				ActivityScoreCalculator c = scoreCalculators.get( ae.getName() );
				if( c != null ) {
					HardSoftScore hs = c.calculateScore( ae, processPlan );
					if( hs != null ) {
						softScore += hs.getSoftScore();
						hardScore += hs.getHardScore();
					}
				}
				if( ae.getName().equals( processPlan.getObjective() ) ) {
					hardScore += 10;
				}
				if( ae.getNext() == null ) {
					if( !ae.getName().equals( "enact" ) ) { //processPlan.getObjective() ) ) {
						hardScore = -Integer.MAX_VALUE;
					}
				}
//				if( processPlan.getObjective().equals( ae.getName() ) ) {
//					hardScore = softScore;
//				}
				
				DMActivityConfiguration activity = new DMActivityConfiguration( ae.getName(), new HashMap<>() );
				for( IDMFitnessCalculator calc : ruleset.rules() ) {
					if( calc == null ) continue;
					DMFitness localFitness = calc.evaluate( activity, problem );
//					System.out.println( "fitness of " + activity.getMethodName() + " = " + localFitness );
					fitness.add( localFitness );
					
				}
			}
		
		}
		
		if( size < 1 ) {
			hardScore = -Integer.MAX_VALUE;
		}
		
		{
//			System.out.println( " (" + softScore + "; " + hardScore + ") ==> " + processPlan.getProcessPath() );
		}
		
		if( log ) {
			System.out.print( processPlan.hashCode() );
			System.out.println( " (" + softScore + "; " + hardScore + ")" );
		}
		
		return HardSoftScore.valueOf(hardScore, softScore);
	}

}
