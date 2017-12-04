package eu.supersede.dm.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;

import eu.supersede.dm.DMActivityConfiguration;
import eu.supersede.dm.DMProblem;
import eu.supersede.dm.DMRuleset;
import eu.supersede.dm.DMSolution;

public class OPOrchestrator
{

	List<String>			queuedRequirements = new ArrayList<>();

	public ProcessChain		process;

	DMSolution				solution;

	DMRuleset				ruleset;

	DMProblem				problem;
	
	
	public OPOrchestrator( DMProblem problem, DMRuleset ruleset ) {
		this.problem = problem;
		this.ruleset = ruleset;
	}
	
	public void setRequirements( List<String> list ) {
		queuedRequirements.addAll( list );
	}

	public ProcessChain plan() {

		System.out.println( "Initializing orchestrator..." );
		SolverFactory<ProcessChain> solverFactory = SolverFactory
				.createFromXmlResource( "processChainSolverConfig.xml" );
		
		solverFactory.getSolverConfig().setScoreDirectorFactoryConfig( new MyScoreDirectorFactoryConfig( problem, ruleset ) );
		solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setEasyScoreCalculatorClass( 
				ProcessPlanEasyScoreCalculator.class );

		Solver<ProcessChain> solver = solverFactory.buildSolver();

		System.out.println( "Generating problem..." );
		ProcessChain unplannedProcess = new ProcessSpaceGenerator().createProcessPlan();

		solver.addEventListener( new SolverEventListener<ProcessChain>() {
			@Override
			public void bestSolutionChanged(BestSolutionChangedEvent<ProcessChain> event) {
				System.out.println( "New best solution found (" + event.getNewBestScore() + "): " + event.getNewBestSolution().hashCode() + " - " + event.getNewBestSolution().getProcessPath() );
				setCurrentSolution( event.getNewBestSolution() );
			}} );

		System.out.println( "Searching for a solution..." );
		TimeDiff diff = new TimeDiff();
		//        ProcessChain plannedProcess = 
		solver.solve(unplannedProcess);
		diff.set();


		return process;

	}

	protected void setCurrentSolution(ProcessChain newBestSolution) {

		process = newBestSolution;

		solution = new DMSolution();

		for( ProcessActivity a = process.getProcessPath(); a != null; a = a.getNext() ) {
			if( a instanceof ActivityExecution ) {
				ActivityExecution ae = (ActivityExecution)a;
				DMActivityConfiguration cfg = new DMActivityConfiguration( ae.getName(), new HashMap<>() );
				solution.addActivity( cfg );
			}
		}

	}
	
	public DMSolution getSolution() {
		return this.solution;
	}
	
}
