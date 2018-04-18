package eu.supersede.orch;

public class Evaluation {
	
	Execution execution;
	
	Optimality optimality;
	
	public Evaluation( Execution exec, Optimality o ) {
		this.execution = exec;
		this.optimality = o;
	}
	
	public Execution getExecution() {
		return this.execution;
	}
	
	public Optimality getOptimality() {
		return this.optimality;
	}
	
}
