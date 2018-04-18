package eu.supersede.orch.jmetal;

import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;

import eu.supersede.orch.MethodInstance;
import eu.supersede.orch.Process;
import eu.supersede.orch.R;

public class ProcessCrossoverOperator implements CrossoverOperator<ProcessSolution> {
	
	void moveRandomMethod( eu.supersede.orch.Process proc ) {
		
		int p1 = 1 + R.get().getRandom().nextInt( proc.workflow().size() -3 );
		
		int p2 = 1 + R.get().getRandom().nextInt( proc.workflow().size() -3 );
		
		while( p2 == p1 ) {
			p2 = 1 + R.get().getRandom().nextInt( proc.workflow().size() -3 );
		}
		
		swap( proc, p1, p2 );
		
	}
	
	private void swap( Process proc, int p1, int p2 ) {
		
		MethodInstance i1 = proc.workflow().get( p1 );
		MethodInstance i2 = proc.workflow().get( p2 );
		proc.workflow().set( p1,  i2 );
		proc.workflow().set( p2,  i1 );
		
	}

	@Override
	public List<ProcessSolution> execute(List<ProcessSolution> source) {
		
		for( ProcessSolution sol : source ) {
			
//			for( eu.supersede.orch.Process proc : sol.getProcesses() ) 
			{
				eu.supersede.orch.Process proc = sol.getProcess();
				
				if( proc.workflow().size() < 4 ) continue; // start + stop + at least 2 to swap
				
				moveRandomMethod( proc );
				
			}
			
		}
		
		return source;
	}

}
