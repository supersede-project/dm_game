package eu.supersede.orch.jmetal;

import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;

import eu.supersede.orch.Evaluation;
import eu.supersede.orch.Evaluator;
import eu.supersede.orch.OptimalityModel;
import eu.supersede.orch.Objective;
import eu.supersede.orch.Optimality;
import eu.supersede.orch.ProcessSpace;
import eu.supersede.orch.Simulator;
import eu.supersede.orch.kb.KnowledgeBase;

public class ProcessPlanningProblem extends AbstractGenericProblem<ProcessSolution> implements Problem<ProcessSolution> {
	
	private static final long serialVersionUID = -5863547705584396036L;

	private Evaluator			eval = new Evaluator();

	private ProcessSpace		processSpace;

	private KnowledgeBase		kb;

	private OptimalityModel				model;

	private List<Objective>		objectives;
	
	private Simulator			simulator;
	

	public ProcessPlanningProblem( ProcessSpace processSpace, KnowledgeBase kb, OptimalityModel model, List<Objective> objectives, Simulator simulator ) {

		this.processSpace	= processSpace;
		this.kb				= kb;
		this.model			= model;
		this.objectives		= objectives;
		this.simulator		= simulator;

		super.setNumberOfObjectives( objectives.size() );

		super.setNumberOfVariables( 1 );

	}

	@Override
	public ProcessSolution createSolution() {

		ProcessSolution ps = new ProcessSolution( this.processSpace.pickRandomProcess() );
		
		ps.setNumberOfObjectives( objectives.size() );
		
		return ps;

	}

	@Override
	public void evaluate( ProcessSolution ps ) {
		
//		for( int i = 0; i < ps.getProcesses().size(); i++ ) 
		{
			
			eu.supersede.orch.Process p = ps.getProcess(); //.getProcesses().get( i );
			
			Optimality o = eval.eval( p, kb.clone(), model, simulator );
			
			ps.saveEvaluation( new Evaluation( eval.getExecution(), o ) );
			
			for( int index = 0; index < objectives.size(); index++ ) {
				
				Objective obj = objectives.get( index );
				
				Double value = o.getValue( obj.getId() );
				if( value == null ) continue; // this should never happen. TODO: check before run
				
				switch( obj.getGoal() ) {
				case ACHIEVE:
					ps.setObjective( index, (value != 0 ? 0 : 1) );
					break;
				case PREVENT:
					ps.setObjective( index, (value != 0 ? 1 : 0) );
					break;
				case MAXIMIZE:
					ps.setObjective( index, -value );
					break;
				case MINIMIZE:
				default:
					ps.setObjective( index, value );
					break;
				}
				
		}
			
//		for( eu.supersede.orch.Process p : ps.getProcesses() )
//		{
//
//			Optimality o = eval.eval( p, kb.clone(), model, simulator );
//			
//			ps.saveEvaluation( new Evaluation( eval.getExecution(), o ) );
//			
//			for( int index = 0; index < objectives.size(); index++ ) {
//				
//				Objective obj = objectives.get( index );
//				
//				Double value = o.getValue( obj.getId() );
//				if( value == null ) continue; // this should never happen. TODO: check before run
//				
//				switch( obj.getGoal() ) {
//				case ACHIEVE:
//					ps.setObjective( index, (value != 0 ? 0 : 1) );
//					break;
//				case PREVENT:
//					ps.setObjective( index, (value != 0 ? 1 : 0) );
//					break;
//				case MAXIMIZE:
//					ps.setObjective( index, -value );
//					break;
//				case MINIMIZE:
//				default:
//					ps.setObjective( index, value );
//					break;
//				}
//				
//			}
			
//			int index = 0;
//			for( Objective obj : objectives ) {
//				Double value = o.getValue( obj.getId() );
//				if( value == null ) continue; // this should never happen. TODO: check before run
//				switch( obj.getGoal() ) {
//				case ACHIEVE:
//					ps.setObjective( index, (value != 0 ? 0 : 1) );
//					break;
//				case PREVENT:
//					ps.setObjective( index, (value != 0 ? 1 : 0) );
//					break;
//				case MAXIMIZE:
//					ps.setObjective( index, -value );
//					break;
//				case MINIMIZE:
//				default:
//					ps.setObjective( index, value );
//					break;
//				}
//				index++;
//			}
		}
		
		System.out.println( ps );
		
	}
}
