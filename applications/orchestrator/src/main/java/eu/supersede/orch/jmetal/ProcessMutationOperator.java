package eu.supersede.orch.jmetal;

import org.uma.jmetal.operator.MutationOperator;

import eu.supersede.orch.MethodDefinition;
import eu.supersede.orch.MethodInstance;
import eu.supersede.orch.ProcessSpace;
import eu.supersede.orch.R;

public class ProcessMutationOperator implements MutationOperator<ProcessSolution> {

	private ProcessSpace space;

	public ProcessMutationOperator(ProcessSpace ps) {
		this.space = ps;
	}

	void mutateRandom( ProcessSolution sol ) {

		// For each process in the solution (1)
//		for( eu.supersede.orch.Process proc : sol.getProcesses() ) 
		{
			eu.supersede.orch.Process proc = sol.getProcess();

			// Decide the maximum number of mutations
			int max = R.get().getRandom().nextInt( 5 );

			for( int i = 0; i < max; i++ ) {

				// If random returns 0, perform an add; if it returns 1, perform a delete
				
				// Perform an add
				if( R.get().getRandom().nextInt( 2 ) > 0 ) {

					// ?
					if( proc.workflow().size() < 4 ) continue;

					// Get a random MethodDefinition
					int index = R.get().getRandom().nextInt( space.methods().size() );

					MethodDefinition md = space.getMethod( index );

					// Get a random position in the workflow (excluding the first and the last)
					int p = 1 + R.get().getRandom().nextInt( proc.workflow().size() -2  );

					MethodInstance prev = proc.workflow().get( p );

					// Try to add the a new instance of the MethodDefinition, is possible
					if( md.satisfiesPreconditions( prev.getDefinition() ) ) {

						MethodInstance mi = md.createRandomInstance();

						proc.add( mi, p );

					}

				}
				// Perform a delete
				else {

					// If there is only start and stop, we can't remove anything
					if( proc.workflow().size() < 3 ) continue;

					// Select a random position in the workflow (excluding the first and the last) and remove it
					int index = 1 + R.get().getRandom().nextInt( proc.workflow().size() -2 );

					proc.workflow().remove( index );

				}

			}

		}

	}

	@Override
	public ProcessSolution execute( ProcessSolution sol ) {

		mutateRandom( sol );

		return sol;
	}

}
