package eu.supersede.orch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.orch.kb.KnowledgeBase;

public class Simulator {
	
	public interface Callback
	{

		void beforeTaskExecution( KnowledgeBase kb, Execution ex );

		void afterTaskExecution( KnowledgeBase kb, Execution ex );

		void beforeSimulationStarts(KnowledgeBase kb, Execution execution);

		void afterSimulationEnds(KnowledgeBase kb, Execution execution);
		
	}
	
	Map<String,List<Worker>>		workers = new HashMap<>();
	
	public Simulator() {
		
		addWorker( new Worker( new MethodDefinition( "start" ), null ) );
		addWorker( new Worker( new MethodDefinition( "stop" ), null ) );
		
	}
	
	public Execution execute( Process process, KnowledgeBase kb, Callback cb ) {
		
		Execution execution = new Execution();
		
		execution.init( kb );
		
		kb.create( "/execution", "path" );
		
		cb.beforeSimulationStarts( kb, execution );
		
		for( MethodInstance mi : process.workflow() ) {
			
			// Move ahead
			execution.setCurrent( mi );
			
//			System.out.println( "====> Step " + execution.getStep() + ": " + execution.getCurrent().getAttribute( "method", "" ) );
			
			// Optimality model evaluation must be executed before producing the output
			cb.beforeTaskExecution( kb, execution );
			
//			System.out.println( "Running..." );
			
			
//			IWorker worker = workers.get( mi.getDefinition().getName() );
//			if( worker != null ) {
//				worker.process( kb, mi );
//			}
			for( Worker p : getWorkers( mi.getDefinition().getName() ) ) {
				p.process( kb, mi );
			}
			
			
//			System.out.println( "> Knowledge Base" );
			
//			execution.kb.print( System.out );
			
			
//			System.out.println( "> Trace" );
			
//			execution.trace.print( System.out );
			
			
//			System.out.println( "" );
			
			
			cb.afterTaskExecution( kb, execution );
			
		}
		
		cb.afterSimulationEnds( kb, execution );
		
		return execution;
		
	}
	
	public void addWorker( Worker pred ) {
//		System.out.println( "Add worker for " + pred.getMethodDefinition().getName() );
		List<Worker> list = workers.get( pred.getMethodDefinition().getName() );
		if( list == null ) {
			list = new ArrayList<>();
			workers.put( pred.getMethodDefinition().getName(), list );
		}
		list.add( pred );
	}
	
	public List<Worker> getWorkers( String methodName ) {
		List<Worker> list = workers.get( methodName );
		if( list == null ) {
			list = new ArrayList<>();
		}
		return list;
	}
	
}
