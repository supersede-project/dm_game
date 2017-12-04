package eu.supersede.dm.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMFitness;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.IDMFitnessCalculator;

public class ModelBasedProcessPlanEasyScoreCalculator2 implements EasyScoreCalculator<ProcessChain> {
	
	
	public static class Indicator {
		
		String name;
		double value;
		
		public Indicator( String name ) {
			this( name, 0 );
		}
		
		public Indicator( String name, double value ) {
			this.name = name;
			this.value = value;
		}
		
		public void setValue( double value ) {
			this.value = value;
		}
		
		public double getValue() {
			return this.value;
		}
		
		public String getName() {
			return this.name;
		}
		
	}
	
	public static class Situation {
		
		private String name;

		public Situation( String name ) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
	}
	
	public static abstract class Aggregator {
		
		private String mnemonic;
		
		Map<String,Indicator> in = new HashMap<>();
		List<Situation> out = new ArrayList<>();
		
		public Aggregator( String mnemonic ) {
			this.mnemonic = mnemonic;
		}
		
		public final String getMnemonic() {
			return this.mnemonic;
		}
		
		public Indicator getIndicator( String rolename ) {
			return in.get( rolename );
		}
		
		public abstract double calc();

		public Aggregator receive( Indicator indicator, String rolename ) {
			in.put( rolename, indicator );
			return this;
		}

		public Aggregator inform( Situation situation ) {
			this.out.add( situation );
			return this;
		}
		
	}
	
	public static class OptimalityModel {
		
		Map<String,Indicator> indicators = new HashMap<>();
		Map<String,Aggregator> aggregators = new HashMap<>();
		Map<String,Situation> situations = new HashMap<>();

		public void add(Indicator indicator) {
			indicators.put( indicator.getName(), indicator );
		}
		
		public void add( Aggregator aggregator ) {
			aggregators.put( aggregator.getMnemonic(), aggregator );
		}

		public Aggregator getAggregator( String mnemonic ) {
			return this.aggregators.get( mnemonic );
		}
		
		public Indicator getIndicator( String name ) {
			return this.indicators.get( name );
		}

		public Situation getSituation( String name ) {
			return situations.get( name );
		}

		public void add( Situation situation ) {
			this.situations.put( situation.getName(), situation );
		}
		
	}
	
	OptimalityModel model = new OptimalityModel();
	
	
	interface ActivityScoreCalculator {
		public HardSoftScore calculateScore( ActivityExecution ae, ProcessChain processPlan );
	}
	
	public static class ModelBasedActivityScoreCalculator implements ActivityScoreCalculator {

		@Override
		public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	public static class ProcessRule {
		
		Map<String,ActivityScoreCalculator> scoreCalculators = new HashMap<>();
		
		DMRuleset ruleset;
		DMProblem problem;
		
		public ProcessRule() {
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
		
		public DMFitness eval( ProcessChain processPlan ) {

			int hardScore = 0;
			int softScore = 0;
			
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
					
					DMActivityConfiguration activity = new DMActivityConfiguration( ae.getName(), new HashMap<>() );
					for( IDMFitnessCalculator calc : ruleset.rules() ) {
						if( calc == null ) continue;
						DMFitness localFitness = calc.evaluate( activity, problem );
//						System.out.println( "fitness of " + activity.getMethodName() + " = " + localFitness );
						fitness.add( localFitness );
						
					}
				}
			
			}
			
			if( size < 1 ) {
				hardScore = -Integer.MAX_VALUE;
			}
			
			return fitness;
		}
		
	}
	
	
	boolean log = false;
	
//	Map<String,ActivityScoreCalculator> scoreCalculators = new HashMap<>();
	
	DMRuleset ruleset;
	DMProblem problem;
	
	
	public ModelBasedProcessPlanEasyScoreCalculator2( DMProblem problem, DMRuleset ruleset ) {
		this();
		this.problem = problem;
		this.ruleset = ruleset;
	}
	
	public ModelBasedProcessPlanEasyScoreCalculator2() {
//		scoreCalculators.put( "ahp-based prioritization", new ActivityScoreCalculator() {
//		@Override
//		public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
//			return HardSoftScore.valueOf( 0, -1 );
//		}} );
//		scoreCalculators.put( "g.a.-based prioritization", new ActivityScoreCalculator() {
//		@Override
//		public HardSoftScore calculateScore(ActivityExecution ae, ProcessChain processPlan) {
//			return HardSoftScore.valueOf( 0, -1 );
//		}} );
		model.add( new Indicator( "number of activities" ) );
		model.add( new Indicator( "number of requirements" ) );
		model.add( new Situation( "high expected process duration" ) );
		model.add( new Aggregator( "estimate expected duration" ) {
			@Override
			public double calc() {
				double actCount = getIndicator( "count" ).getValue();
				double actPairwise = getIndicator( "comparisons" ).getValue();
				return (1 - 1 / (1 + actCount)) * (1 - 1 / (1 + actPairwise));
			}} );
		model.getAggregator( "estimate expected duration" )
			.receive( model.getIndicator( "number of requirements" ), "count" )
			.receive( model.getIndicator( "pairwise" ), "comparisons" )
			.inform( model.getSituation( "highly expected process duration" ) );
		rules.add( new ProcessRule() );
	}
	
	DMFitness calculateFitness( ProcessChain process ) {
		DMFitness fitness = new DMFitness();
		return fitness;
	}
	
	List<ProcessRule> rules = new ArrayList<>();
	
	@Override
	public HardSoftScore calculateScore( ProcessChain processPlan ) {
		
		DMFitnessFunction ff = new DMFitnessFunction();
		
		for( ProcessRule rule : rules ) {
			
			ff.add( rule.eval( processPlan ) );
			
		}
		
		for( String key : ff.map.keySet() ) {
			System.out.println( key + " => " + ff.map.get( key ).factor() );
		}
		
		// TODO: derive hard and soft score from the fitness
		int hardScore = 0;
		int softScore = 0;
		
		
//		int hardScore = 0;
//		int softScore = 0;
//
//		if( log ) {
//			System.out.print( ">>> " + processPlan.getProcessPath() );
//			System.out.println();
//		}
//		
//		DMFitness fitness = new DMFitness();
//		
//		
//		int size = 0;
//		
//		for( ProcessActivity a = processPlan.getProcessPath(); a != null; a = a.getNext() ) {
//			
//			size++;
//			
//			if( a instanceof ActivityExecution ) {
//				ActivityExecution ae = (ActivityExecution)a;
//				softScore++;
//				ActivityScoreCalculator c = scoreCalculators.get( ae.getName() );
//				if( c != null ) {
//					HardSoftScore hs = c.calculateScore( ae, processPlan );
//					if( hs != null ) {
//						softScore += hs.getSoftScore();
//						hardScore += hs.getHardScore();
//					}
//				}
//				if( ae.getName().equals( processPlan.getObjective() ) ) {
//					hardScore += 10;
//				}
//				if( ae.getNext() == null ) {
//					if( !ae.getName().equals( "enact" ) ) { //processPlan.getObjective() ) ) {
//						hardScore = -Integer.MAX_VALUE;
//					}
//				}
////				if( processPlan.getObjective().equals( ae.getName() ) ) {
////					hardScore = softScore;
////				}
//				
//				DMActivityConfiguration activity = new DMActivityConfiguration( ae.getName(), new HashMap<>() );
//				for( IDMFitnessCalculator calc : ruleset.rules() ) {
//					if( calc == null ) continue;
//					DMFitness localFitness = calc.evaluate( activity, problem );
////					System.out.println( "fitness of " + activity.getMethodName() + " = " + localFitness );
//					fitness.add( localFitness );
//					
//				}
//			}
//		
//		}
//		
//		if( size < 1 ) {
//			hardScore = -Integer.MAX_VALUE;
//		}
//		
//		{
////			System.out.println( " (" + softScore + "; " + hardScore + ") ==> " + processPlan.getProcessPath() );
//		}
		
//		if( log ) {
//			System.out.print( processPlan.hashCode() );
//			System.out.println( " (" + softScore + "; " + hardScore + ")" );
//		}
		
		return HardSoftScore.valueOf( hardScore, softScore );
	}

}
