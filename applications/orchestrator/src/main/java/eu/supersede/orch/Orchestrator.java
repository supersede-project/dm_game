package eu.supersede.orch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.orch.jmetal.ProcessPlanner;
import eu.supersede.orch.jmetal.ProcessSolution;
import eu.supersede.orch.kb.KnowledgeBase;

public class Orchestrator
{
	
	ProcessSpace	space		= new ProcessSpace();
	
	KnowledgeBase	kb			= new KnowledgeBase();
	
	Simulator		simulator	= new Simulator();
	
	HistoricalData	h			= new HistoricalData();
	
	OptimalityModel	model		= new OptimalityModel();
	
	Evaluator		eval		= new Evaluator();
	
	
	public Orchestrator() {
		
	}
	
	public ProcessSpace getSpace() {
		return this.space;
	}


	public KnowledgeBase getKB() {
		return this.kb;
	}


	public Simulator getSimulator() {
		return this.simulator;
	}


	public HistoricalData getHistoricalData() {
		return this.h;
	}

	public OptimalityModel getModel() {
		return this.model;
	}
	
	public Optimality calcOptimality( Process process ) {
		
		return evaluate( process ).getOptimality();
	}
	
	public Evaluation evaluate( Process process ) {
		
		System.out.println( process );
		
		Optimality o = eval.eval( process, getKB().clone(), getModel(), getSimulator() );
		
		return new Evaluation( eval.getExecution(), o );
		
	}
	
	public Map<Process,Evaluation> findBest( List<Objective> objectives ) {
		
		ProcessPlanner planner = new ProcessPlanner();
		
		List<ProcessSolution> solutions = planner.plan( getSpace(), getKB(), getModel(), objectives, getSimulator() );
		
		Map<Process,Evaluation> map = new HashMap<>();
		
		for( ProcessSolution sol : solutions ) {
			
			map.put( sol.getProcess(), sol.getEvaluation() );
			
		}
		
		return map;
	}
	
}
