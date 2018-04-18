package eu.supersede.orch.jmetal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

import com.rits.cloning.Cloner;

import eu.supersede.orch.Evaluation;
import eu.supersede.orch.MethodInstance;
import eu.supersede.orch.Process;

public class ProcessSolution implements Solution<Process>  {

	private static final long serialVersionUID = 3435253354600679607L;
	
	List<Double>		objectives		= new ArrayList<>();
	
//	List<Process>		processes		= new ArrayList<>();
	Process				process;
	
	Map<Object,Object>	attributes		= new HashMap<>();
	
	Cloner				cloner			= new Cloner();
	
	
//	Map<Process,Evaluation>	evaluations		= new HashMap<>();
	
	Evaluation			evaluation;
	
	
	public ProcessSolution( Process proc ) {
		this();
		this.process = proc;
//		this.processes.add( proc );
	}

	private ProcessSolution() {
	}

	@Override
	public void setObjective( int index, double value ) {
		objectives.set( index, value );
	}

	@Override
	public double getObjective(int index) {
		return objectives.get( index );
	}

	@Override
	public Process getVariableValue(int index) {
//		return processes.get( index );
		if( index != 0 ) throw new RuntimeException( "ProcessSolution has only 1 variable;" );
		return this.process;
	}

	@Override
	public void setVariableValue(int index, Process value) {
//		processes.set( index, value );
		if( index != 0 ) throw new RuntimeException( "ProcessSolution has only 1 variable;" );
		this.process = value;
	}

	@Override
	public String getVariableValueString(int index) {
		return getVariableValue( index ).toString();
	}

	@Override
	public int getNumberOfVariables() {
		return 1; //processes.size();
	}

	@Override
	public int getNumberOfObjectives() {
		return objectives.size();
	}

	@Override
	public ProcessSolution copy() {
		
		ProcessSolution copy = new ProcessSolution();
		
		for( Double d : this.objectives ) {
			copy.objectives.add( new Double( d.doubleValue() ) );
		}
		
		// automatic deep-copy of the attributes
		for( Object o : this.attributes.keySet() ) {
			copy.attributes.put( cloner.deepClone( o ), cloner.deepClone( attributes.get( o ) ) );
		}
		
		// manual copy of the process(es)
//		for( eu.supersede.orch.Process proc : processes )
		{
			eu.supersede.orch.Process proc = this.process;
			
			eu.supersede.orch.Process p2 = new eu.supersede.orch.Process();
			
			for( MethodInstance inst : proc.workflow() ) {
				
				MethodInstance i2 = inst.copy();
				
				p2.append( i2 );
				
			}
			
			copy.process = p2;
//			copy.processes.add( p2 );
			
		}
		
		return copy;
		
	}

	@Override
	public void setAttribute(Object id, Object value) {
		attributes.put( id, value );
	}

	@Override
	public Object getAttribute(Object id) {
		return attributes.get( id );
	}
	
//	public List<Process> getProcesses() {
//		return this.processes;
//	}
	
	public Process getProcess() {
		return this.process;
	}
	
	public String toString() {
		
		String ret = "Solution " + this.hashCode() + " ";
		
		ret += "(";
		for( Double d : objectives ) {
			ret += d + ";";
		}
		ret += "): ";
		
		ret += this.process;
		
//		for( Process proc : getProcesses() ) {
//			ret += proc;
//		}
		
		return ret;
	}

	public void setNumberOfObjectives(int size) {
		objectives = new ArrayList<Double>();
		for( int i = 0; i < size; i++ ) {
			objectives.add( 0.0 );
		}
	}

//	public void saveEvaluation( Process p, Evaluation e ) {
//		evaluations.put( p, e );
//	}
	
	public void saveEvaluation( Evaluation e ) {
		this.evaluation = e;
//		evaluations.put( p, e );
	}
	
//	public Evaluation getEvaluation( Process p ) {
//		return evaluations.get( p );
//	}
	
	public Evaluation getEvaluation() {
		return this.evaluation;
	}
	
}
